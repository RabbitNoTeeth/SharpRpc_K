package cn.booklish.sharp.config

import cn.booklish.sharp.client.pool.ClientChannelManager
import cn.booklish.sharp.exception.SharpConfigException
import cn.booklish.sharp.server.RpcServerBootStrap
import cn.booklish.sharp.server.compute.RpcRequestManager
import cn.booklish.sharp.server.compute.ServiceBeanFactory
import cn.booklish.sharp.server.register.RegisterTaskManager
import cn.booklish.sharp.server.register.RpcServiceAutoScanner
import cn.booklish.sharp.zookeeper.ZkClient
import java.util.*

/**
 * SharpRpc配置入口
 */
class SharpRpcConfig(fileName:String, val serviceBeanFactory:ServiceBeanFactory) {

    val configMap = HashMap<String,Any>()

    init {
        val resource = this.javaClass.classLoader.getResourceAsStream(fileName)
        val pop = Properties()
        pop.load(resource)
        loadZookeeperConfig(pop)
        loadServerConfig(pop)
        loadClientConfig(pop)
    }

    /**
     * 开始自动配置
     */
    fun autoConfigure(){
        configureZookeeper()
        configureServer()
        configureClient()
    }

    /**
     * 配置zookeeper
     */
    private fun configureZookeeper() {
        ZkClient.init(configMap["base.zookeeper.address"].toString(),
                      configMap["base.zookeeper.poolSize"]!!.toString().toInt(),
                      configMap["base.zookeeper.retryTimes"]!!.toString().toInt(),
                      configMap["base.zookeeper.sleepBetweenRetry"]!!.toString().toInt())
    }

    /**
     * 配置客户端
     */
    private fun configureClient() {
        ClientChannelManager.init(configMap["client.channel.poolSize"]!!.toString().toInt(),
                                  configMap["client.eventLoopGroup.size"]!!.toString().toInt())
    }

    /**
     * 配置服务器
     */
    private fun configureServer() {
        if(configMap["server.enable"].toString().toBoolean()){
            RegisterTaskManager.start(configMap["server.compute.poolSize"]!!.toString().toInt())
            if(configMap["server.autoScan.enable"].toString().toBoolean()){
                RpcServiceAutoScanner(configMap["server.service.autoScan.base"].toString(),
                                      configMap["server.service.register.address"].toString())
                        .scan()
            }
            RpcRequestManager.start(serviceBeanFactory,
                                    configMap["server.compute.async"]!!.toString().toBoolean(),
                                    configMap["server.compute.poolSize"]!!.toString().toInt())
            RpcServerBootStrap.defaultConfigureAndStart(configMap["server.port"]!!.toString().toInt(),
                                                        configMap["client.channel.timeout"]!!.toString().toInt())
        }
    }

    /**
     * 加载zookeeper配置项
     * @param pop
     */
    private fun loadZookeeperConfig(pop: Properties) {

        val zkAddress = pop["base.zookeeper.address"]
        if (zkAddress != null){
            configMap["base.zookeeper.address"] = zkAddress
        }else{
            throw SharpConfigException("[SharpRpc-config]:配置文件错误,配置项[base.zookeeper.address]不能为空")
        }

        configMap["base.zookeeper.retryTimes"] = pop["base.zookeeper.retryTimes"]!!

        configMap["base.zookeeper.sleepBetweenRetry"] = pop["base.zookeeper.sleepBetweenRetry"]!!

        configMap["base.zookeeper.poolSize"] = pop["base.zookeeper.poolSize"]!!

    }

    /**
     * 加载服务器配置
     * @param pop
     */
    private fun loadServerConfig(pop: Properties) {

        val serverEnable = pop["server.enable"]
        if (serverEnable != null) {
            configMap["server.enable"] = serverEnable
            // 开启rpc服务器
            if (serverEnable.toString().toBoolean()) {
                // 开启服务器后,服务的注册地址变为必填
                val registerAddress = pop["server.service.register.address"]
                if (registerAddress != null) {
                    configMap["server.service.register.address"] = registerAddress
                } else {
                    throw SharpConfigException("[SharpRpc-config]:配置文件错误,[server.enable]为true时,配置项[server.service.register.address]不能为空")
                }

                // 判断是否开启自动扫描服务
                val autoScanEnable = pop["server.autoScan.enable"]
                if (autoScanEnable != null) {
                    configMap["server.autoScan.enable"] = autoScanEnable
                    if (autoScanEnable.toString().toBoolean()) {
                        val autoScanBase = pop["server.service.autoScan.base"]
                        if (autoScanBase != null) {
                            configMap["server.service.autoScan.base"] = autoScanBase
                        } else
                            throw SharpConfigException("[SharpRpc-config]:配置文件错误,[server.autoScan.enable]为true时,配置项[server.service.autoScan.base]不能为空")
                    }
                }

                // 设置自定义端口,否则使用默认端口
                configMap["server.port"] = pop["server.port"]!!

                // 设置eventLoopGroup初始大小
                configMap["server.eventLoopGroup.size"] = pop["server.eventLoopGroup.size"]!!

                // 服务器收到Rpc请求后是否进行异步计算
                configMap["server.compute.async"] = pop["server.compute.async"]!!

                // 设置服务器异步处理Rpc请求的线程池大小
                configMap["server.compute.poolSize"] = pop["server.compute.poolSize"]!!

                // 设置服务注册管理器线程池大小
                configMap["server.register.manager.poolSize"] = pop["server.register.manager.poolSize"]!!

            }
        }

    }

    /**
     * 加载客户端配置
     * @param pop
     */
    private fun loadClientConfig(pop: Properties) {

        // 客户端连接池大小
        configMap["client.channel.poolSize"] = pop["client.channel.poolSize"]!!

        // 客户端eventLoopGroup初始大小（0为不设置，采用netty默认值）
        configMap["client.eventLoopGroup.size"] = pop["client.eventLoopGroup.size"]!!

        // 客户端channel连接过期时间大小（单位为s，默认40）
        configMap["client.channel.timeout"] = pop["client.channel.timeout"]!!

    }



}