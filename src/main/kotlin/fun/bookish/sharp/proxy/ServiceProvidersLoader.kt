package `fun`.bookish.sharp.proxy

import `fun`.bookish.sharp.config.ServiceReference
import `fun`.bookish.sharp.model.RegisterValue
import `fun`.bookish.sharp.serialize.GsonUtil

/**
 *
 */
object ServiceProvidersLoader {

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