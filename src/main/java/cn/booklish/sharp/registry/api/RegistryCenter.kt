package cn.booklish.sharp.registry.api


/**
 * @Author: liuxindong
 * @Description:  注册中心接口,后期如果更换服务注册保存介质,只需提供该接口的实现即可
 * @Created: 2017/12/20 9:42
 * @Modified:
 */
interface RegistryCenter {

    fun getChildrenPath(path: String): List<String>

    fun getData(path: String): RegisterInfo

    fun createPath(path: String, data: Any)

    fun updatePath(path: String, data: Any)

    fun deletePath(path: String)

}