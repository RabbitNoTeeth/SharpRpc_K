package `fun`.bookish.sharp.registry.config

import `fun`.bookish.sharp.constant.SharpConstants
import `fun`.bookish.sharp.registry.api.RegistryCenter
import `fun`.bookish.sharp.registry.api.RegistryCenterType

/**
 * 注册中心配置类
 */
class RegistryConfig {

    var type = SharpConstants.DEFAULT_REGISTRY_TYPE

    var host = SharpConstants.DEFAULT_REGISTRY_HOST

    var port = SharpConstants.DEFAULT_REGISTRY_PORT

    var timeout = SharpConstants.DEFAULT_REGISTRY_TIMEOUT

    var registryCenter: RegistryCenter? = null

    fun type(type:RegistryCenterType):RegistryConfig{
        this.type = type
        return this
    }

    fun host(host:String):RegistryConfig{
        this.host = host
        return this
    }

    fun port(port:Int):RegistryConfig{
        this.port = port
        return this
    }

    fun timeout(timeout:Int):RegistryConfig{
        this.timeout = timeout
        return this
    }

    fun registryCenter(registryCenter: RegistryCenter):RegistryConfig{
        this.registryCenter = registryCenter
        return this
    }

}