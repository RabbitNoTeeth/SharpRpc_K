package `fun`.bookish.sharp.registry.support.redis

import `fun`.bookish.sharp.model.RegisterValue
import `fun`.bookish.sharp.registry.api.RegistryCenter
import `fun`.bookish.sharp.registry.config.RegistryConfig
import `fun`.bookish.sharp.serialize.GsonUtil
import redis.clients.jedis.Jedis

/**
 * redis服务注册中心
 */
class RedisRegistryCenter (private val registryConfig: RegistryConfig):RegistryCenter {

    val jedis = Jedis(registryConfig.host, registryConfig.port, registryConfig.timeout)

    override fun register(key: String, value: RegisterValue) {
        jedis.sadd(key, GsonUtil.objectToJson(value))
    }

    override fun unregister(key: String, value: RegisterValue) {
        jedis.srem(key,GsonUtil.objectToJson(value))
    }

    override fun getProviders(key: String): Set<RegisterValue> {
        return jedis.smembers(key).map { GsonUtil.jsonToObject(it,RegisterValue::class.java) }.toSet()
    }

    override fun address(): String {
        return "${registryConfig.type.value}: ${registryConfig.host}:${registryConfig.port}"
    }

}