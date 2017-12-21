package cn.booklish.sharp.registry.support.zookeeper

import cn.booklish.sharp.registry.api.RegistryCenter
import cn.booklish.sharp.registry.api.RegisterInfo
import cn.booklish.sharp.registry.exception.*
import cn.booklish.sharp.serialize.KryoUtil
import org.apache.commons.lang3.StringUtils
import org.apache.log4j.Logger
import org.apache.zookeeper.CreateMode
import org.apache.zookeeper.ZooDefs

/**
 * @Author: liuxindong
 * @Description:  zookeeper客户端,封装curator提供的zookeeper客户端,重新封装并提供更加便于调用的节点操作方法
 * @Created: 2017/12/13 9:05
 * @Modified:
 */
class ZookeeperRegistryCenter(zkConnectionConfig:ZkConnectionConfig) : RegistryCenter {

    private val logger: Logger = Logger.getLogger(this.javaClass)

    private val connectionPool = ZkConnectionPoolFactory(zkConnectionConfig)

    /**
     * 获取指定节点的所有子节点
     */
    override fun getChildrenPath(path: String): List<String>{

        val connection = connectionPool.getConnection()
        try {
            return connection.children.forPath(path)
        } catch (e: Exception) {
            throw GetChildrenPathException("zookeeper-获取服务子节点列表失败",e)
        } finally {
            connectionPool.releaseConnection(connection)
        }

    }

    /**
     * 获取指定节点的数据
     */
    override fun getData(path: String): RegisterInfo {

        val connection = connectionPool.getConnection()
        try {
            return KryoUtil.readObjectFromByteArray(connection.data.forPath(path), RegisterInfo::class.java)
        } catch (e: Exception) {
            throw GetPathDataException("zookeeper-获取服务[$path]的数据失败", e)
        } finally {
            connectionPool.releaseConnection(connection)
        }

    }

    /**
     * 创建节点
     */
    override fun createPath(path: String, data: Any) {

        val connection = connectionPool.getConnection()
        try {
            if (!checkPathExists(path)) {
                checkParentExits(path)
                connection.create().withMode(CreateMode.PERSISTENT).withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                        .forPath(path, KryoUtil.writeObjectToByteArray(data))
            } else {
                updatePath(path, data)
            }
        } catch (e: Exception) {
            throw CreatePathException("zookeeper-创建服务[$path]失败", e)
        } finally {
            connectionPool.releaseConnection(connection)
        }

    }

    private fun checkParentExits(path: String) {
        checkAndCreateParent(path.substringBeforeLast("/"))
    }

    private fun checkAndCreateParent(path: String) {
        if (StringUtils.isNotBlank(path)) {
            if (!checkPathExists(path)) {
                val parent = path.substringBeforeLast("/")
                if (StringUtils.isNotBlank(parent)) {
                    checkAndCreateParent(parent)
                }
                createPath(path, "")
            }
        }
    }

    /**
     * 更新节点数据
     */
    override fun updatePath(path: String, data: Any) {

        val connection = connectionPool.getConnection()
        try {
            if (checkPathExists(path))
                connection.setData().forPath(path, KryoUtil.writeObjectToByteArray(data))
        } catch (e: Exception) {
            throw UpdatePathException("zookeeper-更新服务[$path]数据失败", e)
        } finally {
            connectionPool.releaseConnection(connection)
        }

    }

    /**
     * 删除节点
     */
    override fun deletePath(path: String) {

        val connection = connectionPool.getConnection()
        try {
            if (checkPathExists(path))
                connection.delete().withVersion(-1).forPath(path)
        } catch (e: Exception) {
            throw DeletePathException("zookeeper-删除服务[$path]失败", e)
        } finally {
            connectionPool.releaseConnection(connection)
        }

    }

    /**
     * 检查节点是否存在
     */
    override fun checkPathExists(path: String): Boolean {

        val connection = connectionPool.getConnection()
        try {
            return connection.checkExists().forPath(path) != null
        } catch (e: Exception) {
            throw CheckPathExistsException("zookeeper-检查服务[$path]是否存在失败")
        } finally {
            connectionPool.releaseConnection(connection)
        }

    }


}