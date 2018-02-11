package `fun`.bookish.sharp.proxy

import `fun`.bookish.sharp.config.ServiceReference
import `fun`.bookish.sharp.model.RegisterValue
import `fun`.bookish.sharp.serialize.GsonUtil

/**
 *
 */
object ServiceProvidersLoader {

    fun getProviders(serviceReference: ServiceReference<*>):MutableList<RegisterValue>{

        val serviceKey = serviceReference.serviceKey

        val key = serviceReference.getRegisterKey()

        val registryCenters = serviceReference.registryCenters

        val providers = mutableSetOf<RegisterValue>()

        for (registryCenter in registryCenters){
            try {
                providers.addAll(registryCenter.getProviders(key))
            }catch (e:Exception){
                //ignore this registryCenter
                continue
            }
        }

        if(providers.isEmpty()){
            throw IllegalArgumentException("there is no available provider of service \"$serviceKey\"")
        }

        return providers.sortedBy { it.weight }.toMutableList()

    }


}