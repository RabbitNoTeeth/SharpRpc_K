package cn.booklish.sharp.registry.zookeeper

import cn.booklish.sharp.constant.Constants
import cn.booklish.sharp.exception.*
import cn.booklish.sharp.registry.api.RegisterClient
import cn.booklish.sharp.registry.api.RegisterInfo
import cn.booklish.sharp.serialize.KryoSerializer
import org.apache.commons.lang3.StringUtils
import org.apache.curator.framework.CuratorFramework
import org.apache.curator.framework.CuratorFrameworkFactory
import org.apache.curator.framework.api.CuratorWatcher
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
class ZookeeperClient: RegisterClient {

    var zkAddress:String = Constants.DEFAULT_ZOOKEEPER_ADDRESS
    var connectionPoolSize:Int = Constants.DEFAULT_ZOOKEEPER_CONNECTION_POOL_SIZE
    var zkRetryTimes:Int = Constants.DEFAULT_ZOOKEEPER_RETRY_TIMES
    var zkSleepBetweenRetry:Int = Constants.DEFAULT_ZOOKEEPER_SLEEP_BETWEEN_RETRY

    private val logger: Logger = Logger.getLogger(this.javaClass)

    private val pool = ConnectionPool()

    fun init(zkAddress:String?,connectionPoolSize:Int?,zkRetryTimes:Int?,zkSleepBetweenRetry:Int?): ZookeeperClient {
        zkAddress?.let { this.zkAddress = it }
        connectionPoolSize?.let { this.connectionPoolSize = it }
        zkRetryTimes?.let { this.zkRetryTimes = it }
        zkSleepBetweenRetry?.let { this.zkSleepBetweenRetry = it }
        return this
    }

    /**
     * 获取指定节点的所有子节点
     */
    override fun getChildrenPath(path: String): List<String>{

        try {
            return pool.getConnection().children.forPath(path)
        } catch (e: Exception) {
            logger.warn("获取zookeeper子节点列表失败")
            throw RuntimeException(e)
        }

    }

    /**
     * 获取指定节点的数据
     */
    override fun getData(path: String): RegisterInfo {

        try {
            return KryoSerializer.readObjectFromByteArray(pool.getConnection().data.forPath(path), RegisterInfo::class.java)
        } catch (e: Exception) {
            logger.error("获取zookeeper路径[$path]数据失败")
            throw GetZkPathDataException("获取zookeeper路径[$path]数据失败", e)
        }

    }

    /**
     * 创建节点
     */
    override fun createPath(path: String, data: Any) {

        try {
            if (!checkPathExists(path)) {
                checkParentExits(path)
                pool.getConnection().create().withMode(CreateMode.PERSISTENT).withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                        .forPath(path, KryoSerializer.writeObjectToByteArray(data))
            } else {
                updatePath(path, data)
            }
        } catch (e: Exception) {
            logger.error("创建zookeeper路径[$path]失败")
            throw CreateZkPathException("创建zookeeper路径[$path]失败", e)
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

        try {

            if (checkPathExists(path))
                pool.getConnection().setData().forPath(path, KryoSerializer.writeObjectToByteArray(data))

        } catch (e: Exception) {
            logger.error("更新指定的zookeeper路径[$path]失败")
            throw UpdateZkPathException("更新指定的zookeeper路径[$path]失败", e)
        }

    }

    /**
     * 删除节点
     */
    override fun deletePath(path: String) {

        try {

            if (checkPathExists(path))
                pool.getConnection().delete().withVersion(-1).forPath(path)

        } catch (e: Exception) {
            logger.error("删除指定的zookeeper路径[$path]失败")
            throw DeleteZkPathException("删除指定的zookeeper路径[$path]失败", e)
        }

    }

    /**
     * 检查节点是否存在
     */
    private fun checkPathExists(path: String): Boolean {

        try {
            return pool.getConnection().checkExists().forPath(path) != null
        } catch (e: Exception) {
            logger.error("检查指定的zookeeper路径[$path]是否存在失败")
            throw CheckZkPathExistsException("检查指定的zookeeper路径[$path]是否存在失败")
        }

    }

    /**
     * 内部连接池
     */
    private inner class ConnectionPool{

        val pool = arrayOfNulls<CuratorFramework>(connectionPoolSize)
        val locks = Array(connectionPoolSize,{ Any() })

        fun getConnection(): CuratorFramework{
            val index = Random().nextInt(connectionPoolSize)
            val connection = pool[index]
            if(connection!=null && connection.state == CuratorFrameworkState.STARTED){
                return connection
            }
            synchronized(locks[index]) {
                val connection2 = pool[index]
                if (connection2!=null && connection2.state == CuratorFrameworkState.STARTED) {
                    return connection2
                }
                val newConnection = createConnection()
                pool[index] = newConnection
                return newConnection
            }
        }

        private fun createConnection(): CuratorFramework {
            val connection = CuratorFrameworkFactory.newClient(zkAddress, RetryNTimes(zkRetryTimes, zkSleepBetweenRetry))
            connection.start()
            return connection
        }

    }


}