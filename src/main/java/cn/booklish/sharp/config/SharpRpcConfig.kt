package cn.booklish.sharp.config

import cn.booklish.sharp.client.pool.ClientChannelManager
import cn.booklish.sharp.exception.SharpConfigException
import cn.booklish.sharp.server.RpcServerBootStrap
import cn.booklish.sharp.server.compute.RpcRequestManager
import cn.booklish.sharp.server.compute.ServiceBeanFactory
import cn.booklish.sharp.server.register.RegisterTaskManager
import cn.booklish.sharp.server.register.RpcServiceAutoScanner
import cn.booklish.sharp.zookeeper.ZkClient
import org.apache.log4j.Logger
import java.util.*

/**
 * @Author: liuxindong
 * @Description:  SharpRpc配置类,读取配置文件并提供自动配置方法
 * @Created: 2017/12/13 8:51
 * @Modified:
 */
class SharpRpcConfig(private val fileName:String, private val serviceBeanFactory:ServiceBeanFactory) {

    private val logger: Logger = Logger.getLogger(this.javaClass)

    private val configMap = HashMap<String,Any>()

    init {
        val resource = this.javaClass.classLoader.getResourceAsStream(fileName)
        val pop = Properties()
        pop.load(resource)
        loadZookeeperConfig(pop)
        loadServerConfig(pop)
        loadClientConfig(pop)
        logger.info("[Sharp-config]: 配置文件加载完成")
    }

    /**
     * 开始自动配置,可以在调用该方法前对serverBootstrap进行拓展,实现一定程度的定制化
     */
    fun autoConfigure(){
        logger.info("[Sharp-config]: 开始自动化配置 >>>>>>>")
        configureZookeeper()
        configureServer()
        configureClient()
        logger.info("[Sharp-config]: 完成自动化配置 -------")
    }

    /**
     * 配置zookeeper
     */
    private fun configureZookeeper() {
        ZkClient.init(configMap["base.zookeeper.address"].toString(),
                      configMap["base.zookeeper.poolSize"]?.toString()?.toInt(),
                      configMap["base.zookeeper.retryTimes"]?.toString()?.toInt(),
                      configMap["base.zookeeper.sleepBetweenRetry"]?.toString()?.toInt())
        logger.info("[Sharp-config]: Zookeeper客户端ZkClient配置完成")
    }

    /**
     * 配置客户端
     */
    private fun configureClient() {
        ClientChannelManager.init(configMap["client.channel.poolSize"]?.toString()?.toInt(),
                                  configMap["client.eventLoopGroup.size"]?.toString()?.toInt())
        logger.info("[Sharp-config]: 客户端配置完成")
    }

    /**
     * 配置服务器
     */
    private fun configureServer() {
        if(configMap["server.enable"].toString().toBoolean()){
            logger.info("[Sharp-config]: 开始服务器配置>>>")

            RegisterTaskManager.start(configMap["server.compute.poolSize"]?.toString()?.toInt())
            logger.info("[Sharp-config]: RegisterTaskManager管理器启动成功")

            if(configMap["server.autoScan.enable"].toString().toBoolean()){
                RpcServiceAutoScanner(configMap["server.service.autoScan.base"].toString(),
                        configMap["server.service.register.address"].toString())
                        .scan()
                logger.info("[Sharp-config]: RpcServiceAutoScanner服务扫描器配启动成功")
            }

            RpcRequestManager.start(serviceBeanFactory,
                    configMap["server.compute.async"]?.toString()?.toBoolean(),
                    configMap["server.compute.poolSize"]?.toString()?.toInt())
            logger.info("[Sharp-config]: RpcRequestManager管理器启动成功")

            RpcServerBootStrap.defaultConfigureAndStart(configMap["server.port"]?.toString()?.toInt(),
                    configMap["client.channel.timeout"]?.toString()?.toInt())
            logger.info("[Sharp-config]: RpcServerBootStrap引导启动成功,服务器启动完成")
        }
    }

    /**
     * 加载zookeeper配置项
     * @param pop
     */
    private fun loadZookeeperConfig(pop: Properties) {

        configMap["base.zookeeper.address"] =
                pop["base.zookeeper.address"]?:throw SharpConfigException("[SharpRpc-config]:配置文件错误,配置项[base.zookeeper.address]不能为空")

        pop["base.zookeeper.retryTimes"]?.let { configMap["base.zookeeper.retryTimes"] = it }

        pop["base.zookeeper.sleepBetweenRetry"]?.let { configMap["base.zookeeper.sleepBetweenRetry"] = it }

        pop["base.zookeeper.poolSize"]?.let { configMap["base.zookeeper.poolSize"] = it }

    }

    /**
     * 加载服务器配置
     * @param pop
     */
    private fun loadServerConfig(pop: Properties) {

        pop["server.enable"]?.let { serverEnable ->
            configMap["server.enable"] = serverEnable
            // 开启rpc服务器
            if (serverEnable.toString().toBoolean()) {

                // 开启服务器后,服务的注册地址变为必填
                configMap["server.service.register.address"] =
                        pop["server.service.register.address"]?:throw SharpConfigException("[SharpRpc-config]:配置文件错误,[server.enable]为true时,配置项[server.service.register.address]不能为空")

                // 判断是否开启自动扫描服务
                pop["server.autoScan.enable"]?.let { autoScanEnable ->
                    configMap["server.autoScan.enable"] = autoScanEnable
                    if (autoScanEnable.toString().toBoolean()) {
                        configMap["server.service.autoScan.base"] =
                                pop["server.service.autoScan.base"]?:throw SharpConfigException("[SharpRpc-config]:配置文件错误,[server.autoScan.enable]为true时,配置项[server.service.autoScan.base]不能为空")
                    }
                }

                // 设置自定义端口,否则使用默认端口
                pop["server.port"]?.let { configMap["server.port"] = it }
                // 服务器收到Rpc请求后是否进行异步计算
                pop["server.compute.async"]?.let { configMap["server.compute.async"] = it }
                // 设置服务器异步处理Rpc请求的线程池大小
                pop["server.compute.poolSize"]?.let { configMap["server.compute.poolSize"] = it }
                // 设置服务注册管理器线程池大小
                pop["server.register.manager.poolSize"]?.let { configMap["server.register.manager.poolSize"] = it }

            }
        }

    }

    /**
     * 加载客户端配置
     * @param pop
     */
    private fun loadClientConfig(pop: Properties) {

        // 客户端连接池大小
        pop["client.channel.poolSize"]?.let { configMap["client.channel.poolSize"] = it }
        // 客户端eventLoopGroup初始大小（0为不设置，采用netty默认值）
        pop["client.eventLoopGroup.size"]?.let { configMap["client.eventLoopGroup.size"] = it }
        // 客户端channel连接过期时间大小（单位为s，默认40）
        pop["client.channel.timeout"]?.let { configMap["client.channel.timeout"] = it }

    }

}