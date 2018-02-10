package `fun`.bookish.sharp.registry.support.redis

import `fun`.bookish.sharp.registry.api.RegistryCenter
import `fun`.bookish.sharp.registry.config.RegistryConfig
import redis.clients.jedis.Jedis

/**
 * redis服务注册中心
 */
class RedisRegistryCenter (private val registryConfig: RegistryConfig):RegistryCenter {

    val jedis = Jedis(registryConfig.host, registryConfig.port, registryConfig.timeout)

    override fun register(serviceName: String, address: String) {
        jedis.sadd(serviceName, address)
    }

    override fun unregister(serviceName: String, address: String) {
        jedis.srem(serviceName,address)
    }

    override fun getProviders(serviceName: String): Set<String> {
        return jedis.smembers(serviceName)
    }

    override fun address(): String {
        return "${registryConfig.type.value}: ${registryConfig.host}:${registryConfig.port}"
    }

}