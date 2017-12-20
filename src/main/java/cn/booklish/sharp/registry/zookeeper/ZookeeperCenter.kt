package cn.booklish.sharp.registry.zookeeper

import cn.booklish.sharp.constant.Constants
import cn.booklish.sharp.exception.*
import cn.booklish.sharp.registry.api.RegistryCenter
import cn.booklish.sharp.registry.api.RegisterInfo
import cn.booklish.sharp.serialize.KryoUtil
import org.apache.commons.lang3.StringUtils
import org.apache.curator.framework.CuratorFramework
import org.apache.curator.framework.CuratorFrameworkFactory
import org.apache.curator.framework.imps.CuratorFrameworkState
import org.apache.curator.retry.RetryNTimes
import org.apache.log4j.Logger
import org.apache.zookeeper.CreateMode
import org.apache.zookeeper.ZooDefs
import java.util.*

/**
 * @Author: liuxindong
 * @Description:  zookeeper客户端,封装curator提供的zookeeper客户端,重新封装并提供更加便于调用的节点操作方法
 * @Created: 2017/12/13 9:05
 * @Modified:
 */
class ZookeeperCenter(zkAddress:String = Constants.DEFAULT_ZOOKEEPER_ADDRESS,
                      zkRetryTimes:Int = Constants.DEFAULT_ZOOKEEPER_RETRY_TIMES,
                      zkSleepBetweenRetry:Int = Constants.DEFAULT_ZOOKEEPER_SLEEP_BETWEEN_RETRY
) : RegistryCenter {

    private val logger: Logger = Logger.getLogger(this.javaClass)

    private val connectionPool = ZkConnectionPoolFactory(zkAddress,zkRetryTimes,zkSleepBetweenRetry)


    /**
     * 获取指定节点的所有子节点
     */
    override fun getChildrenPath(path: String): List<String>{

        val connection = connectionPool.getConnection()
        try {
            return connection.children.forPath(path)
        } catch (e: Exception) {
            logger.warn("获取zookeeper子节点列表失败")
            throw RuntimeException(e)
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
            logger.error("获取zookeeper路径[$path]数据失败")
            throw GetZkPathDataException("获取zookeeper路径[$path]数据失败", e)
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
            logger.error("创建zookeeper路径[$path]失败")
            throw CreateZkPathException("创建zookeeper路径[$path]失败", e)
        } finally {
            connectionPool.releaseConnection(connection)
        }

    }

    private fun checkParentExits(path: String) {

        try {
            checkAndCreateParent(path.substringBeforeLast("/"))
        } catch (e: Exception) {
            throw RuntimeException(e)
        }

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
            logger.error("更新指定的zookeeper路径[$path]失败")
            throw UpdateZkPathException("更新指定的zookeeper路径[$path]失败", e)
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
            logger.error("删除指定的zookeeper路径[$path]失败")
            throw DeleteZkPathException("删除指定的zookeeper路径[$path]失败", e)
        } finally {
            connectionPool.releaseConnection(connection)
        }

    }

    /**
     * 检查节点是否存在
     */
    private fun checkPathExists(path: String): Boolean {

        val connection = connectionPool.getConnection()
        try {
            return connection.checkExists().forPath(path) != null
        } catch (e: Exception) {
            logger.error("检查指定的zookeeper路径[$path]是否存在失败")
            throw CheckZkPathExistsException("检查指定的zookeeper路径[$path]是否存在失败")
        } finally {
            connectionPool.releaseConnection(connection)
        }

    }


}