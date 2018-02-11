package `fun`.bookish.sharp.registry.api

import `fun`.bookish.sharp.model.RegisterValue


/**
 * @Author: liuxindong
 * @Description:  注册中心接口,后期如果更换服务注册保存介质,只需提供该接口的实现即可
 * @Created: 2017/12/20 9:42
 * @Modified:
 */
interface RegistryCenter {

    /**
     * 注册服务
     */
    fun register(key: String, value: RegisterValue)

    /**
     * 注销服务
     */
    fun unregister(key: String, value: RegisterValue)

    /**
     * 获取服务提供者
     */
    fun getProviders(key: String): Set<RegisterValue>

    /**
     * 获取注册中心地址
     */
    fun address(): String

}