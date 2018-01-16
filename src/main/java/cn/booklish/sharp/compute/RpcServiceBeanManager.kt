package cn.booklish.sharp.compute

import java.util.concurrent.ConcurrentHashMap

/**
 * Rpc服务实体类管理器
 */
object RpcServiceBeanManager {

    private val map = ConcurrentHashMap<String,Any>()

    fun add(bean :Any){
        map[bean.javaClass.typeName] = bean
    }

    fun <T> get(serviceClass: Class<T>): T?{
        return map[serviceClass.typeName] as? T
    }

}