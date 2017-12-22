package cn.booklish.sharp.config

import cn.booklish.sharp.compute.RpcRequestComputeManager
import cn.booklish.sharp.compute.ServiceBeanFactory
import cn.booklish.sharp.exception.SharpConfigException
import cn.booklish.sharp.proxy.ServiceProxyFactory
import cn.booklish.sharp.registry.api.RegistryCenter
import cn.booklish.sharp.registry.api.RegisterTaskManager
import cn.booklish.sharp.registry.api.RegistryCenterType
import cn.booklish.sharp.registry.api.RpcServiceAutoScanner
import cn.booklish.sharp.registry.support.redis.RedisConnectionConfig
import cn.booklish.sharp.registry.support.redis.RedisRegistryCenter
import cn.booklish.sharp.registry.support.zookeeper.ZkConnectionConfig
import cn.booklish.sharp.registry.support.zookeeper.ZookeeperRegistryCenter
import cn.booklish.sharp.remoting.netty4.core.*
import cn.booklish.sharp.serialize.api.RpcSerializer
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
    val zookeeperConfig = ZkConnectionConfig()
    val redisConfig = RedisConnectionConfig()
    val clientConfig = ClientConfig()
    val serverConfig = ServerConfig()

    /**
     * 关闭服务器功能
     */
    fun disableServer():SharpRpcConfig{
        this.serverConfig.serverEnable = false
        return this
    }

    /**
     * 启动自动服务扫描器
     */
    fun enableAutoScanner():SharpRpcConfig{
        this.serverConfig.autoScannerEnable = true
        return this
    }

    /**
     * 设置自动扫描基础路径
     */
    fun setAutoScanBasePath(autoScanBasePath:String): SharpRpcConfig {
        this.serverConfig.autoScanBasePath = autoScanBasePath
        return this
    }

    /**
     * 设置自动扫描服务注册地址
     */
    fun setAutoScanRegisterAddress(autoScanRegisterAddress:String): SharpRpcConfig {
        this.serverConfig.autoRegisterPath = autoScanRegisterAddress
        return this
    }

    /**
     * 设置注册中心
     */
    fun setRegistyrCenter(registryCenterType: RegistryCenterType, registerAddress:String):SharpRpcConfig{

        this.registryCenter = when(registryCenterType){

            RegistryCenterType.ZOOKEEPER -> {
                this.zookeeperConfig.address = registerAddress
                ZookeeperRegistryCenter(this.zookeeperConfig)
            }

            RegistryCenterType.REDIS -> {
                this.redisConfig.address = registerAddress
                RedisRegistryCenter(this.redisConfig)
            }

        }
        return this

    }

    /**
     * 设置序列化实现
     */
    fun setRpcSerializer(rpcSerializer: RpcSerializer):SharpRpcConfig{
        this.clientConfig.rpcSerializer = rpcSerializer
        this.serverConfig.rpcSerializer = rpcSerializer
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
            fillConfigBeans()
            logger.info("[Sharp-config]: properties配置文件加载完成")
        }
        return this
    }

    /**
     * 填充配置类
     */
    private fun fillConfigBeans() {

        /*----填充zookeeper配置类----*/
        configMap["zookeeper.address"]?:let { this.zookeeperConfig.address = it.toString() }
        configMap["zookeeper.retryTimes"]?.let { this.zookeeperConfig.retryTimes = it.toString().toInt() }
        configMap["zookeeper.sleepBetweenRetry"]?.let { this.zookeeperConfig.sleepBetweenRetry = it.toString().toInt() }
        configMap["zookeeper.session.timeout"]?.let { this.zookeeperConfig.sessionTimeout = it.toString().toInt() }
        configMap["zookeeper.connection.timeout"]?.let { this.zookeeperConfig.connectionTimeOut = it.toString().toInt() }
        configMap["zookeeper.pool.maxTotal"]?.let { this.zookeeperConfig.poolConfig.maxTotal = it.toString().toInt() }
        configMap["zookeeper.pool.maxIdle"]?.let { this.zookeeperConfig.poolConfig.maxIdle = it.toString().toInt() }
        configMap["zookeeper.pool.minIdle"]?.let { this.zookeeperConfig.poolConfig.minIdle = it.toString().toInt() }

        /*----填充redis配置类----*/
        configMap["redis.address"]?.let { this.redisConfig.address = it.toString() }
        configMap["redis.connection.timeout"]?.let { this.redisConfig.connectionTimeOut = it.toString().toInt() }
        configMap["redis.pool.maxTotal"]?.let { this.redisConfig.poolConfig.maxTotal = it.toString().toInt() }
        configMap["redis.pool.maxIdle"]?.let { this.redisConfig.poolConfig.maxIdle = it.toString().toInt() }
        configMap["redis.pool.minIdle"]?.let { this.redisConfig.poolConfig.minIdle = it.toString().toInt() }

        /*----填充client配置类----*/
        configMap["client.channel.timeout"]?.let {
            this.clientConfig.channelTimeout = it.toString().toInt()
            this.serverConfig.clientChannelTimeout = it.toString().toInt()
        }
        configMap["client.channel.pool.maxTotal"]?.let { this.clientConfig.channelPoolConfig.maxTotal = it.toString().toInt() }
        configMap["client.channel.pool.maxIdle"]?.let { this.clientConfig.channelPoolConfig.maxIdle = it.toString().toInt() }
        configMap["client.channel.pool.minIdle"]?.let { this.clientConfig.channelPoolConfig.minIdle = it.toString().toInt() }

        /*----填充server配置类----*/
        configMap["server.enable"]?.let { this.serverConfig.serverEnable = it.toString().toBoolean() }
        configMap["server.listen.port"]?.let { this.serverConfig.listenPort = it.toString().toInt() }
        configMap["server.compute.manager.async"]?.let { this.serverConfig.asyncComputeRpcRequest = it.toString().toBoolean() }
        configMap["server.compute.manager.threadPoolSize"]?.let { this.serverConfig.computeThreadPoolSize = it.toString().toInt() }
        configMap["server.registry.manager.threadPoolSize"]?.let { this.serverConfig.registerThreadPoolSize = it.toString().toInt() }
        configMap["server.autoScan.enable"]?.let { this.serverConfig.autoScannerEnable = it.toString().toBoolean() }
        configMap["server.autoScan.basePath"]?.let { this.serverConfig.autoScanBasePath = it.toString() }
        configMap["server.autoScan.registry.address"]?.let { this.serverConfig.autoRegisterPath = it.toString() }
    }

    /**
     * 开始自动配置
     */
    fun configure(){
        //--------------------------------配置注册中心--------------------------------

        logger.info("[Sharp-config]: 开始Sharp配置 >>>>>>>>>>>>>>>")
        if(this.registryCenter==null){
            this.registryCenter = initRegistryCenterFromProperties()
        }
        this.serverConfig.registryCenter = this.registryCenter
        this.clientConfig.registryCenter = this.registryCenter
        logger.info("[Sharp-config]: 1.RegistryCenter注册中心配置完成")


        //--------------------------------配置客户端--------------------------------

        ServiceProxyFactory.init(this.clientConfig)
        logger.info("[Sharp-config]: 2.ServiceProxyFactory客户端服务代理工厂配置完成")

        ClientChannelPool.init(this.clientConfig)
        logger.info("[Sharp-config]: 3.ClientChannelManager客户端channel管理器配置完成")

        Client.init(this.clientConfig)
        logger.info("[Sharp-config]: 4.Client客户端配置完成")

        //--------------------------------配置服务端--------------------------------

        if(this.serverConfig.serverEnable){
            logger.info("[Sharp-config]: 5.启用服务器,开始配置服务器")

            RegisterTaskManager.start(this.serverConfig)
            logger.info("[Sharp-config]:  5.1 RegisterTaskManager服务注册管理器配置成功")

            if(this.serverConfig.autoScannerEnable){
                this.serverConfig.autoScanBasePath ?:throw SharpConfigException("启用服务自动扫描器后,未设置扫描基础路径")
                this.serverConfig.autoRegisterPath ?:throw SharpConfigException("启用服务自动扫描器后,未设置服务注册地址")
                RpcServiceAutoScanner.scan(this.serverConfig)
                logger.info("[Sharp-config]: 5.2 RpcServiceAutoScanner服务自动扫描器配配置成功")
            }else{
                logger.info("[Sharp-config]: 5.2 未开启RpcServiceAutoScanner服务自动扫描器,跳过配置")
            }

            RpcRequestComputeManager.start(this.serverConfig,serviceBeanFactory)
            logger.info("[Sharp-config]: 5.3 RpcRequestManager服务请求管理器启动成功")

            Server.init(this.serverConfig).start()
            logger.info("[Sharp-config]: 5.4 RpcServerBootStrap引导启动成功,服务器启动完成")

        }else{
            logger.info("[Sharp-config]: 5 未启用服务器端,跳过配置")
        }

        logger.info("[Sharp-config]: 6 Sharp配置完成 >>>>>>>>>>>>>>>")
    }

    /**
     * 根据配置文件创建注册中心
     */
    private fun initRegistryCenterFromProperties(): RegistryCenter {
        val type = configMap["registry.center.type"]
        type?:throw SharpConfigException("未配置RegistryCenter注册中心类型")
        return when(type.toString()){
            "zookeeper" -> ZookeeperRegistryCenter(this.zookeeperConfig)
            "redis" -> RedisRegistryCenter(this.redisConfig)
            else -> throw SharpConfigException("不支持的RegistryCenter注册中心类型")
        }
    }

}