package `fun`.bookish.sharp.manage.bean

import java.util.concurrent.ConcurrentHashMap

object ServiceManager {

    private val cache = ConcurrentHashMap<String,TempBean>()

    fun add(serviceKey:String, serviceInterface:Class<*>, serviceRef :Any){
        cache.putIfAbsent(serviceKey, TempBean(serviceInterface,serviceRef))
    }

    fun get(serviceKey:String): TempBean?{
        return cache[serviceKey]
    }

    data class TempBean(val serviceInterface:Class<*>, val serviceRef :Any)

}