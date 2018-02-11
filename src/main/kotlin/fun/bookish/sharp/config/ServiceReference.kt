package `fun`.bookish.sharp.config

import `fun`.bookish.sharp.protocol.config.ProtocolConfig
import `fun`.bookish.sharp.proxy.ServiceProxyFactory
import `fun`.bookish.sharp.registry.api.RegistryCenter
import `fun`.bookish.sharp.registry.api.RegistryCenterType
import `fun`.bookish.sharp.registry.config.RegistryConfig
import `fun`.bookish.sharp.registry.support.redis.RedisRegistryCenter

/**
 * 服务引用
 */
class ServiceReference<T> {

    val registryCenters:MutableList<RegistryCenter> = mutableListOf()

    lateinit var serviceInterface:Class<T>

    var version = "1.0.0"

    fun setRegistry(registry: RegistryConfig): ServiceReference<T>{
        val registryCenter = registry.registryCenter?:createRegistryCenter(registry)
        this.registryCenters.add(registryCenter)
        return this
    }

    fun setRegistries(registries: List<RegistryConfig>): ServiceReference<T>{
        for(registry in registries){
            val registryCenter = registry.registryCenter?:createRegistryCenter(registry)
            this.registryCenters.add(registryCenter)
        }
        return this
    }

    private fun createRegistryCenter(registryConfig: RegistryConfig):RegistryCenter{
        return when(registryConfig.type) {
            RegistryCenterType.REDIS -> {
                RedisRegistryCenter(registryConfig)
            }
            RegistryCenterType.ZOOKEEPER -> {
                // TODO 添加zookeeper注册中心实现
                RedisRegistryCenter(registryConfig)
            }
        }
    }

    fun setInterface(clazz:Class<T>): ServiceReference<T>{
        this.serviceInterface = clazz
        return this
    }

    fun setVersion(version:String): ServiceReference<T>{
        this.version = version
        return this
    }

    /**
     * 获取服务
     */
    fun get():T{
        return ServiceProxyFactory.getService(this)
    }

    /**
     * 服务直连
     */
    fun get(protocol: ProtocolConfig):T{
        return ServiceProxyFactory.getService(this,protocol)
    }


}