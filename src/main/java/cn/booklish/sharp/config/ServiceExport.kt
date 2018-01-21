package cn.booklish.sharp.config

import cn.booklish.sharp.protocol.config.ProtocolConfig
import cn.booklish.sharp.registry.api.RegistryCenter
import cn.booklish.sharp.registry.api.RegistryCenterType
import cn.booklish.sharp.registry.config.RegistryConfig
import cn.booklish.sharp.registry.manager.RegisterTaskManager
import cn.booklish.sharp.registry.support.redis.RedisRegistryCenter
import cn.booklish.sharp.remoting.netty4.core.Server
import redis.clients.jedis.Jedis

/**
 * 服务引用
 */
class ServiceExport<T> {

    val registryCenters:MutableList<RegistryCenter> = mutableListOf()

    val protocols:MutableList<ProtocolConfig> = mutableListOf()

    lateinit var serviceInterface:Class<T>

    lateinit var serviceRef:Any

    var version = "1.0.0"

    fun setRegistry(registry: RegistryConfig):ServiceExport<T>{
        val registryCenter = registry.registryCenter?:createRegistryCenter(registry)
        this.registryCenters.add(registryCenter)
        return this
    }

    fun setRegistries(registries: List<RegistryConfig>):ServiceExport<T>{
        for(registry in registries){
            val registryCenter = registry.registryCenter?:createRegistryCenter(registry)
            this.registryCenters.add(registryCenter)
        }
        return this
    }

    private fun createRegistryCenter(registryConfig: RegistryConfig):RegistryCenter{
        return when(registryConfig.type) {
            RegistryCenterType.REDIS -> {
                val jedis = Jedis(registryConfig.host, registryConfig.port, registryConfig.timeout)
                RedisRegistryCenter(jedis)
            }
            RegistryCenterType.ZOOKEEPER -> {
                //暂时设置
                val jedis = Jedis(registryConfig.host, registryConfig.port, registryConfig.timeout)
                RedisRegistryCenter(jedis)
            }
        }
    }

    fun setProtocol(protocol: ProtocolConfig):ServiceExport<T>{
        this.protocols.add(protocol)
        return this
    }

    fun setProtocols(protocols: List<ProtocolConfig>):ServiceExport<T>{
        for (protocol in protocols){
            this.protocols.add(protocol)
        }
        return this
    }

    fun setInterface(clazz:Class<T>):ServiceExport<T>{
        this.serviceInterface = clazz
        return this
    }

    fun setRef(ref:Any):ServiceExport<T>{
        this.serviceRef = ref
        return this
    }

    fun setVersion(version:String):ServiceExport<T>{
        this.version = version
        return this
    }

    /**
     * 注册并暴露服务
     */
    fun export(){
        //注册服务到注册中心
        RegisterTaskManager.submit(this)
        //启动netty监听
        Server(this).start()
    }


}