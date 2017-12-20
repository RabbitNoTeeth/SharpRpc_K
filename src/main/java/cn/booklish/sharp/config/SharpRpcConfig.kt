package cn.booklish.sharp.config

import cn.booklish.sharp.remoting.netty4.core.ClientChannelManager
import cn.booklish.sharp.compute.RpcRequestManager
import cn.booklish.sharp.compute.ServiceBeanFactory
import cn.booklish.sharp.constant.Constants
import cn.booklish.sharp.exception.SharpConfigException
import cn.booklish.sharp.proxy.ServiceProxyFactory
import cn.booklish.sharp.registry.api.RegistryCenter
import cn.booklish.sharp.registry.api.RegisterTaskManager
import cn.booklish.sharp.registry.api.RegistryCenterType
import cn.booklish.sharp.registry.api.RpcServiceAutoScanner
import cn.booklish.sharp.registry.zookeeper.ZookeeperCenter
import cn.booklish.sharp.remoting.netty4.core.Client
import cn.booklish.sharp.remoting.netty4.core.Server
import cn.booklish.sharp.serialize.api.RpcSerializer
import org.apache.commons.pool2.impl.GenericObjectPoolConfig
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

    private val configMap = HashMap<String,Any>()
    private var registryCenter: RegistryCenter? = null
    private var rpcSerializer: RpcSerializer? = null
    private var enableServer = true
    private var enableAutoScanner = false
    private var autoScanBasePath:String? = null
    private var autoScanRegisterAddress:String? = null
    private var loadPropertiesState = false
    val zookeeperPoolConfig = GenericObjectPoolConfig()
    val redisPoolConfig = GenericObjectPoolConfig()
    val channelPoolConfig = GenericObjectPoolConfig()

    /**
     * 关闭服务器功能
     */
    fun disableServer():SharpRpcConfig{
        this.enableServer = false
        return this
    }

    /**
     * 启动自动服务扫描器
     */
    fun enableAutoScanner():SharpRpcConfig{
        this.enableAutoScanner = true
        return this
    }

    /**
     * 设置自动扫描基础路径
     */
    fun setAutoScanBasePath(autoScanBasePath:String): SharpRpcConfig {
        this.autoScanBasePath = autoScanBasePath
        return this
    }

    /**
     * 设置自动扫描服务注册地址
     */
    fun setAutoScanRegisterAddress(autoScanRegisterAddress:String): SharpRpcConfig {
        this.autoScanRegisterAddress = autoScanRegisterAddress
        return this
    }

    /**
     * 设置注册中心
     */
    fun setRegistyrCenter(registryCenterType: RegistryCenterType, registerAddress:String):SharpRpcConfig{

        this.registryCenter = when(registryCenterType){

            RegistryCenterType.ZOOKEEPER -> {
                if(loadPropertiesState){
                    ZookeeperCenter(registerAddress,
                                    configMap["zookeeper.retryTimes"]?.toString()?.toInt()?:Constants.DEFAULT_ZOOKEEPER_RETRY_TIMES,
                                    configMap["zookeeper.sleepBetweenRetry"]?.toString()?.toInt()?:Constants.DEFAULT_ZOOKEEPER_SLEEP_BETWEEN_RETRY,
                                    zookeeperPoolConfig
                    )
                }else{
                    ZookeeperCenter(registerAddress,config = zookeeperPoolConfig)
                }
            }

            RegistryCenterType.REDIS -> null
        }
        return this

    }

    /**
     * 设置序列化实现
     */
    fun setRpcSerializer(rpcSerializer: RpcSerializer):SharpRpcConfig{
        this.rpcSerializer = rpcSerializer
        return this
    }

    /**
     * 加载配置文件
     */
    fun loadProperties(fileName:String):SharpRpcConfig {
        val resource = this.javaClass.classLoader.getResourceAsStream(fileName)
        resource?.let { res ->
            val pop = Properties()
            pop.load(res)
            for(key in pop.stringPropertyNames()){
                pop[key]?.let {
                    configMap.put(key, it)
                }
            }
            this.loadPropertiesState = true
            logger.info("[Sharp-config]: properties配置文件加载完成")
        }
        return this
    }

    /**
     * 开始自动配置
     */
    fun configure(){
        if(loadPropertiesState){
            configureWithProperties()
        }else{
            configureWithoutProperties()
        }
    }

    /**
     * 有配置文件的配置
     */
    private fun configureWithProperties() {

        if(autoScanBasePath ==null){
            configMap["server.autoScan.basePath"]?.let { autoScanBasePath = it.toString() }
        }
        if(autoScanRegisterAddress ==null){
            configMap["server.autoScan.registry.address"]?.let { autoScanRegisterAddress = it.toString() }
        }

        //--------------------------------配置注册中心--------------------------------

        logger.info("[Sharp-config]: 开始Sharp配置 >>>>>>>>>>>>>>>")
        if(registryCenter==null){
            val registryCenterType:Any? = configMap["registry.center.type"]
            registryCenterType?:throw SharpConfigException("未配置RegistryCenter注册中心类型")
            registryCenter = when(registryCenterType.toString()){
                "zookeeper" -> {
                    configMap["zookeeper.address"]?:throw SharpConfigException("未配置RegistryCenter注册中心地址")
                    ZookeeperCenter(configMap["zookeeper.address"]?.toString()?:Constants.DEFAULT_ZOOKEEPER_ADDRESS,
                            configMap["zookeeper.retryTimes"]?.toString()?.toInt()?:Constants.DEFAULT_ZOOKEEPER_RETRY_TIMES,
                            configMap["zookeeper.sleepBetweenRetry"]?.toString()?.toInt()?:Constants.DEFAULT_ZOOKEEPER_SLEEP_BETWEEN_RETRY,
                            zookeeperPoolConfig)
                }
                "redis" -> {
                    null
                }
                else -> throw SharpConfigException("未配置RegistryCenter注册中心")
            }
        }
        logger.info("[Sharp-config]: 1.RegistryCenter注册中心配置完成")

        //--------------------------------配置客户端--------------------------------

        ServiceProxyFactory.init(registryCenter!!)
        logger.info("[Sharp-config]: 2.ServiceProxyFactory客户端服务代理工厂配置完成")

        ClientChannelManager.init(channelPoolConfig)
        logger.info("[Sharp-config]: 3.ClientChannelManager客户端channel管理器配置完成")

        rpcSerializer?.let { Client.rpcSerializer = it }
        Client.init()
        logger.info("[Sharp-config]: 4.Client客户端配置完成")

        //--------------------------------配置服务端--------------------------------

        if(enableServer){
            logger.info("[Sharp-config]: 5.启用服务器,开始配置服务器")

            configMap["server.register.manager.threadPoolSize"]?.let { RegisterTaskManager.threadPoolSize = it.toString().toInt() }
            RegisterTaskManager.start(registryCenter!!)
            logger.info("[Sharp-config]: 5.1 RegisterTaskManager服务注册管理器配置成功")

            if(enableAutoScanner){
                autoScanBasePath ?:throw SharpConfigException("启用服务自动扫描器后,未设置扫描基础路径")
                autoScanRegisterAddress ?:throw SharpConfigException("启用服务自动扫描器后,未设置服务注册地址")
                RpcServiceAutoScanner(autoScanBasePath!!, autoScanRegisterAddress!!).scan()
                logger.info("[Sharp-config]: 5.2 RpcServiceAutoScanner服务自动扫描器配配置成功")
            }else{
                logger.info("[Sharp-config]: 5.2 未开启RpcServiceAutoScanner服务自动扫描器,跳过配置")
            }

            configMap["server.compute.manager.async"]?.let { RpcRequestManager.async = it.toString().toBoolean() }
            configMap["server.compute.manager.threadPoolSize"]?.let { RpcRequestManager.threadPoolSize = it.toString().toInt() }
            RpcRequestManager.start(serviceBeanFactory)
            logger.info("[Sharp-config]: 5.3 RpcRequestManager服务请求管理器启动成功")

            configMap["server.listen.port"]?.let { Server.port = it.toString().toInt() }
            configMap["client.channel.timeout"]?.let { Server.clientChannelTimeout = it.toString().toInt() }
            rpcSerializer?.let { Server.rpcSerializer = it }
            Server.init().start()
            logger.info("[Sharp-config]: 5.4 RpcServerBootStrap引导启动成功,服务器启动完成")

        }else{
            logger.info("[Sharp-config]: 5 未启用服务器端,跳过配置")
        }

        logger.info("[Sharp-config]: 6 Sharp配置完成 >>>>>>>>>>>>>>>")

    }

    /**
     * 无配置文件的配置
     */
    private fun configureWithoutProperties() {

        //--------------------------------配置注册中心--------------------------------

        logger.info("[Sharp-config]: 开始Sharp配置 >>>>>>>>>>>>>>>")
        registryCenter?:throw SharpConfigException("未配置RegistryCenter注册中心")
        logger.info("[Sharp-config]: 1.RegistryCenter注册中心配置完成")


        //--------------------------------配置客户端--------------------------------

        ServiceProxyFactory.init(registryCenter!!)
        logger.info("[Sharp-config]: 2.ServiceProxyFactory客户端服务代理工厂配置完成")

        ClientChannelManager.init(channelPoolConfig)
        logger.info("[Sharp-config]: 3.ClientChannelManager客户端channel管理器配置完成")

        rpcSerializer?.let { Client.rpcSerializer = it }
        Client.init()
        logger.info("[Sharp-config]: 4.Client客户端配置完成")

        //--------------------------------配置服务端--------------------------------

        if(enableServer){
            logger.info("[Sharp-config]: 5.启用服务器,开始配置服务器")

            RegisterTaskManager.start(registryCenter!!)
            logger.info("[Sharp-config]:  5.1 RegisterTaskManager服务注册管理器配置成功")

            if(enableAutoScanner){
                autoScanBasePath ?:throw SharpConfigException("启用服务自动扫描器后,未设置扫描基础路径")
                autoScanRegisterAddress ?:throw SharpConfigException("启用服务自动扫描器后,未设置服务注册地址")
                RpcServiceAutoScanner(autoScanBasePath!!, autoScanRegisterAddress!!).scan()
                logger.info("[Sharp-config]: 5.2 RpcServiceAutoScanner服务自动扫描器配配置成功")
            }else{
                logger.info("[Sharp-config]: 5.2 未开启RpcServiceAutoScanner服务自动扫描器,跳过配置")
            }

            RpcRequestManager.start(serviceBeanFactory)
            logger.info("[Sharp-config]: 5.3 RpcRequestManager服务请求管理器启动成功")

            rpcSerializer?.let { Server.rpcSerializer = it }
            Server.init().start()
            logger.info("[Sharp-config]: 5.4 RpcServerBootStrap引导启动成功,服务器启动完成")

        }else{
            logger.info("[Sharp-config]: 5 未启用服务器端,跳过配置")
        }

        logger.info("[Sharp-config]: 6 Sharp配置完成 >>>>>>>>>>>>>>>")

    }

}