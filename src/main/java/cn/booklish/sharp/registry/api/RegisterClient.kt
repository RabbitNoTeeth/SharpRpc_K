package cn.booklish.sharp.registry.api


interface RegisterClient {

    fun getChildrenPath(path: String): List<String>

    fun getData(path: String): RegisterInfo

    fun createPath(path: String, data: Any)

    fun updatePath(path: String, data: Any)

    fun deletePath(path: String)

}