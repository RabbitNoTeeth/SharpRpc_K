package `fun`.bookish.sharp.registry.support.zookeeper

import `fun`.bookish.sharp.constant.SharpConstants
import `fun`.bookish.sharp.model.RegisterValue
import `fun`.bookish.sharp.protocol.api.ProtocolName
import `fun`.bookish.sharp.registry.api.RegistryCenter
import `fun`.bookish.sharp.registry.config.RegistryConfig
import `fun`.bookish.sharp.serialize.GsonUtil
import org.apache.curator.framework.CuratorFrameworkFactory
import org.apache.curator.retry.RetryNTimes
import org.apache.zookeeper.CreateMode
import org.apache.zookeeper.ZooDefs

/**
 * zookeeper注册中心实现
 */
class ZkRegistryCenter(private val registryConfig: RegistryConfig): RegistryCenter {

    private val connection = CuratorFrameworkFactory.newClient("${registryConfig.host}:${registryConfig.port}",
            RetryNTimes(SharpConstants.DEFAULT_ZK_RETRY_TIMES, SharpConstants.DEFAULT_ZK_RETRY_SLEEP)).apply { start() }

    override fun register(key: String, value: RegisterValue) {
        val path = if(value.protocol == ProtocolName.RMI){
            "/$key/${value.address.substringAfter("rmi://").substringBefore("/")}"
        }else{
            "/$key/${value.address}"
        }
        if(checkNodeExists(path)){
            updateNode(path,GsonUtil.objectToJson(value))
        }else{
            checkAndCreateParentNode(path.substringBeforeLast("/"))
            createNode(path,GsonUtil.objectToJson(value))
        }
    }

    override fun unregister(key: String, value: RegisterValue) {
        val path = "/$key/${value.address}"
        if(checkNodeExists(path)){
            deleteNode(path)
        }
    }

    override fun getProviders(key: String): Set<RegisterValue> {
        val path = "/$key"
        return if(checkNodeExists(path)){
            val children = getChildrenNodes(path)
            if(children.isNotEmpty()){
                children.map { GsonUtil.jsonToObject(getData("/$key/$it"),RegisterValue::class.java) }.toSet()
            }else{
                emptySet()
            }
        }else{
            emptySet()
        }
    }

    override fun address(): String {
        return "${registryConfig.type.value}: ${registryConfig.host}:${registryConfig.port}"
    }

    /**
     * 检查父节点是否存在,不存在则创建
     */
    private fun checkAndCreateParentNode(path: String) {
        if (path.isNotBlank()) {
            if (!checkNodeExists(path)) {
                val parent = path.substringBeforeLast("/")
                if (parent.isNotBlank()) {
                    checkAndCreateParentNode(parent)
                }
                createNode(path, "")
            }
        }
    }

    /**
     * 检查节点是否存在
     */
    private fun checkNodeExists(path: String): Boolean {
        return connection.checkExists().forPath(path) != null
    }

    /**
     * 获取子节点
     */
    private fun getChildrenNodes(path: String):Set<String>{
        return connection.children.forPath(path).toSet()
    }

    /**
     * 获取节点数据
     */
    private fun getData(path: String): String {
        return String(connection.data.forPath(path),Charsets.UTF_8)
    }

    /**
     * 创建节点
     */
    private fun createNode(path: String, data: String) {
        connection.create().withMode(CreateMode.PERSISTENT).withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE).forPath(path, data.toByteArray(Charsets.UTF_8))
    }

    /**
     * 更新节点
     */
    private fun updateNode(path: String, data: String) {
        connection.setData().forPath(path, data.toByteArray(Charsets.UTF_8))
    }

    /**
     * 删除节点
     */
    private fun deleteNode(path: String) {
        if (checkNodeExists(path))
            connection.delete().withVersion(-1).forPath(path)
    }

}
