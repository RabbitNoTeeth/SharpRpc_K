package cn.booklish.sharp.config

import cn.booklish.sharp.compute.RpcRequestComputeManager
import cn.booklish.sharp.protocol.config.ProtocolConfig
import cn.booklish.sharp.proxy.ServiceProxyFactory
import cn.booklish.sharp.registry.api.RegistryCenterType
import cn.booklish.sharp.registry.config.RegistryConfig
import cn.booklish.sharp.registry.manager.RegisterTaskManager
import cn.booklish.sharp.registry.support.redis.RedisRegistryCenter
import cn.booklish.sharp.remoting.netty4.config.ClientConfig
import cn.booklish.sharp.remoting.netty4.config.ServerConfig
import cn.booklish.sharp.remoting.netty4.core.Client
import cn.booklish.sharp.remoting.netty4.core.Server
import cn.booklish.sharp.serialize.config.SerializerConfig
import org.apache.log4j.Logger
import redis.clients.jedis.Jedis

/**
 * SharpRpc配置类
 */
class SharpRpcConfig {

    private val logger: Logger = Logger.getLogger(this.javaClass)

    @Volatile
    private var registryReady = false

    @Volatile
    private var clientReady = false

    @Volatile
    private var serverReady = false

    val serializerConfig = SerializerConfig()

    val registry = RegistryConfig()

    val protocol = ProtocolConfig()

    val client = ClientConfig()

    val server = ServerConfig()

    /**
     * 注册服务
     */
    @JvmOverloads
    fun register(serviceBean:Any,version: String = "1.0.0"){
        checkServerReady()
        val registerInfo = RegisterTaskManager.RegisterInfo(serviceBean,protocol.host+":"+protocol.port,version)
        RegisterTaskManager.submit(registerInfo)
    }

    /**
     * 获取服务
     */
    @JvmOverloads
    fun <T> getService(clazz: Class<T>,version: String = "1.0.0"): T?{
        checkClientReady()
        return ServiceProxyFactory.getService(clazz,version) as? T
    }


    /**
     * 检查服务端状态
     */
    private fun checkServerReady() {
        checkRegistryReady()
        if(!serverReady){
            server.rpcSerializer = serializerConfig.rpcSerializer
            server.registryCenter = registry.registryCenter
            RegisterTaskManager.init(registry,protocol)
            RpcRequestComputeManager.start(server)
            Server.init(server)
            serverReady = true
        }
    }

    /**
     * 检查注册中心状态
     */
    private fun checkRegistryReady() {
        if(!registryReady){
            when(registry.type){
                RegistryCenterType.REDIS -> {
                   val jedis = Jedis(registry.address,registry.port,registry.timeout)
                   registry.registryCenter = RedisRegistryCenter(jedis)
                }
                RegistryCenterType.ZOOKEEPER -> {

                }
            }
            registryReady = true
        }
    }

    /**
     * 检查客户端状态
     */
    private fun checkClientReady() {
        if(!clientReady){
            client.rpcSerializer = serializerConfig.rpcSerializer
            client.registryCenter = registry.registryCenter
            Client.init(client)
            ServiceProxyFactory.init(client,protocol)
            clientReady = true
        }
    }


}