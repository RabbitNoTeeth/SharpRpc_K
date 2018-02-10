package cn.booklish.sharp.config

import cn.booklish.sharp.proxy.ServiceProxyFactory
import cn.booklish.sharp.registry.api.RegistryCenter
import cn.booklish.sharp.registry.api.RegistryCenterType
import cn.booklish.sharp.registry.config.RegistryConfig
import cn.booklish.sharp.registry.support.redis.RedisRegistryCenter
import redis.clients.jedis.Jedis

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
     * 注册并暴露服务
     */
    fun get():T{
        return ServiceProxyFactory.getService(this)
    }


}