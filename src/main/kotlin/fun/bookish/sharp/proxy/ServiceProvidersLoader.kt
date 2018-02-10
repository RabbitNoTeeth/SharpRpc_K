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

        val providers = mutableSetOf<String>()

        for (registryCenter in registryCenters){
            try {
                providers.addAll(registryCenter.getProviders(key))
            }catch (e:Exception){
                //ignore this registryCenter
                continue
            }
        }

        if(providers.isEmpty()){
            throw IllegalArgumentException("there is no available provider of service \"${serviceReference.serviceInterface.typeName}\"")
        }

        return providers.map { GsonUtil.jsonToObject(it,RegisterValue::class.java) }
                .sortedBy { it.weight }.toMutableList()

    }


}