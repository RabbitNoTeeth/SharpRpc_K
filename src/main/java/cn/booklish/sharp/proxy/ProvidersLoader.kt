package cn.booklish.sharp.proxy

import cn.booklish.sharp.config.ServiceReference
import cn.booklish.sharp.model.RegisterValue
import cn.booklish.sharp.serialize.GsonUtil

/**
 *
 */
object ProvidersLoader {

    fun getProviders(serviceReference: ServiceReference<*>):MutableList<RegisterValue>{

        val serviceName = serviceReference.serviceInterface.typeName.replace(".","/",false)

        val key = "SharpRpc://" + serviceName + "?version=" + serviceReference.version

        val registryCenters = serviceReference.registryCenters

        var providers: Set<String>? = null

        for (registryCenter in registryCenters){
            providers = registryCenter.getProviders(key)
            if(providers.isNotEmpty()){
                break
            }
        }

        if(providers==null || providers.isEmpty()){
            throw IllegalArgumentException("there is no available provider of service \"${serviceReference.serviceInterface.typeName}\"")
        }

        return providers.map { GsonUtil.jsonToObject(it,RegisterValue::class.java) }
                .sortedBy { it.weight }.toMutableList()

    }


}