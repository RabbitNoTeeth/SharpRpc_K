package `fun`.bookish.sharp.manage

import java.util.concurrent.ConcurrentHashMap

object ProviderManager {


    private val cache = ConcurrentHashMap<String,ProviderManageBean>()

    fun cache(provider: ProviderManageBean){
        cache.putIfAbsent(provider.serviceAddress,provider)
    }

    fun remove(key: String){
        cache.remove(key)
    }

    fun incConCount(key: String,connection: String){
        val provider = cache[key]
        if(provider != null){
            provider.connections.getAndIncrement()
            provider.connectionsList.add(connection)
        }
    }

    fun decConCount(key: String,connection: String){
        val provider = cache[key]
        if(provider != null){
            provider.connections.getAndDecrement()
            provider.connectionsList.remove(connection)
        }
    }

    fun getAvailableProviders(): List<ProviderManageBean>{
        return cache.values.toList()
    }

}