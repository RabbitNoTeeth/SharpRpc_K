package cn.booklish.sharp.config

import cn.booklish.sharp.remoting.netty4.core.ClientChannelManager
import cn.booklish.sharp.compute.RpcRequestManager
import cn.booklish.sharp.compute.ServiceBeanFactory
import cn.booklish.sharp.proxy.ServiceProxyFactory
import cn.booklish.sharp.registry.api.RegisterClient
import cn.booklish.sharp.registry.api.RegisterTaskManager
import cn.booklish.sharp.registry.api.RpcServiceAutoScanner
import cn.booklish.sharp.registry.zookeeper.ZookeeperClient
import cn.booklish.sharp.remoting.netty4.core.Server
import org.apache.log4j.Logger
import java.util.*

/**
 * @Author: liuxindong
 * @Description:  SharpRpc配置类,读取配置文件并提供自动配置方法
 * @Created: 2017/12/13 8:51
 * @Modified:
 */
class SharpRpcConfig(private val serviceBeanFactory: ServiceBeanFactory) {

    private val logger: Logger = Logger.getLogger(this.javaClass)

    val configMap = HashMap<String,Any>()

    fun loadProperties(fileName:String) {
        val resource = this.javaClass.classLoader.getResourceAsStream(fileName)
        resource?.let { res ->
            val pop = Properties()
            pop.load(res)
            for(key in pop.stringPropertyNames()){
                pop[key]?.let {
                    configMap.put(key, it)
                }
            }
            logger.info("[Sharp-config]: 配置文件加载完成")
        }
    }

    /**
     * 自动配置
     */
    fun autoConfigure(serverEnable:Boolean = true,
                      registryType: RegistryType = RegistryType.ZOOKEEPER,
                      registerAddress:String = "47.94.206.26:2181"
    ){
        logger.info("[Sharp-config]: 开始自动化配置 >>>>>>>")
        val registryClient = configureRegistryClient(registryType, registerAddress)
        registryClient?:throw RuntimeException("RegisterClient创建失败!")
        configureClient(registryClient)
        configureServer(serverEnable,registryClient)
        logger.info("[Sharp-config]: 完成自动化配置 -------")
    }

    /**
     * 配置注册服务的存储类型,是redis还是zookeeper
     */
    private fun configureRegistryClient(registryType: RegistryType, registerAddress: String):RegisterClient? {
        return when(registryType){
            RegistryType.ZOOKEEPER -> {
               ZookeeperClient().init(configMap["zookeeper.address"]?.toString(),
                                        configMap["zookeeper.poolSize"]?.toString()?.toInt(),
                                        configMap["zookeeper.retryTimes"]?.toString()?.toInt(),
                                        configMap["zookeeper.sleepBetweenRetry"]?.toString()?.toInt()
               )
            }
            RegistryType.REDIS -> {
                null
            }
        }
    }

    /**
     * 配置客户端
     */
    private fun configureClient(registryClient: RegisterClient) {
        ServiceProxyFactory.init(registryClient)
        ClientChannelManager.init(configMap["client.channel.poolSize"]?.toString()?.toInt())
        logger.info("[Sharp-config]: 客户端配置完成")
    }

    /**
     * 配置服务器
     */
    private fun configureServer(serverEnable:Boolean,registryClient: RegisterClient) {
        val serverEnable = configMap["server.enable"]?.toString()?.toBoolean()?:serverEnable
        if(serverEnable){
            logger.info("[Sharp-config]: 开始服务器配置>>>")

            RegisterTaskManager.start(configMap["server.compute.poolSize"]?.toString()?.toInt(),registryClient)
            logger.info("[Sharp-config]: RegisterTaskManager管理器启动成功")

            val autoScanEnable = configMap["server.autoScan.enable"]?.toString()?.toBoolean()?:false
            if(autoScanEnable){
                RpcServiceAutoScanner(configMap["server.service.autoScan.base"].toString(),
                                      configMap["server.service.register.address"].toString()
                ).scan()
                logger.info("[Sharp-config]: RpcServiceAutoScanner服务扫描器配启动成功")
            }

            RpcRequestManager.start(serviceBeanFactory,
                    configMap["server.compute.async"]?.toString()?.toBoolean(),
                    configMap["server.compute.poolSize"]?.toString()?.toInt())
            logger.info("[Sharp-config]: RpcRequestManager管理器启动成功")

            Server.init(configMap["server.port"]?.toString()?.toInt(),
                                    configMap["client.channel.timeout"]?.toString()?.toInt()
            ).start()
            logger.info("[Sharp-config]: RpcServerBootStrap引导启动成功,服务器启动完成")
        }
    }

}