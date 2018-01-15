package cn.booklish.sharp.registry.api


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
    fun register(serviceName: String, address: String)

    /**
     * 注销服务
     */
    fun unregister(serviceName: String, address: String)

    /**
     * 获取服务提供者
     */
    fun getProviders(serviceName: String): Set<String>

}