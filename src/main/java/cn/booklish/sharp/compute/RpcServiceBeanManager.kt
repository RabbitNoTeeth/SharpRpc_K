package cn.booklish.sharp.compute

import java.util.concurrent.ConcurrentHashMap

/**
 * Rpc服务实体类管理器
 */
object RpcServiceBeanManager {

    private val map = ConcurrentHashMap<String,Any>()

    fun add(clazz:Class<*>,bean :Any){
        map[clazz.typeName] = bean
    }

    fun <T> get(serviceClass: Class<T>): T?{
        return map[serviceClass.typeName] as? T
    }

}