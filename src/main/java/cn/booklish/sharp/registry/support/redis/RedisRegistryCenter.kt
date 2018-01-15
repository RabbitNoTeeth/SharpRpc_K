package cn.booklish.sharp.registry.support.redis

import cn.booklish.sharp.registry.api.RegistryCenter
import redis.clients.jedis.Jedis

/**
 * redis服务注册中心
 */
class RedisRegistryCenter (address:String,port:Int):RegistryCenter {

    val jedis:Jedis = Jedis(address,port)

    override fun register(serviceName: String, address: String) {
        jedis.sadd(serviceName, address)
    }

    override fun unregister(serviceName: String, address: String) {
        jedis.srem(serviceName,address)
    }

    override fun getProviders(serviceName: String): Set<String> {
        return jedis.smembers(serviceName)
    }

}