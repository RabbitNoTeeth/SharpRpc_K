package cn.booklish.sharp.registry.zookeeper

import org.apache.commons.pool2.BasePooledObjectFactory
import org.apache.commons.pool2.PooledObject
import org.apache.commons.pool2.impl.DefaultPooledObject
import org.apache.curator.framework.CuratorFramework
import org.apache.curator.framework.CuratorFrameworkFactory
import org.apache.curator.framework.imps.CuratorFrameworkState
import org.apache.curator.retry.RetryNTimes

/**
 * @Author: liuxindong
 * @Description:  zookeeper连接工厂
 * @Created: 2017/12/20 14:35
 * @Modified:
 */
class ZkConnectionFactory(private val zkAddress:String,
                          private val zkRetryTimes:Int,
                          private val zkSleepBetweenRetry:Int
): BasePooledObjectFactory<CuratorFramework>() {

    override fun create(): CuratorFramework {
        val connection = CuratorFrameworkFactory.newClient(zkAddress, RetryNTimes(zkRetryTimes, zkSleepBetweenRetry))
        connection.start()
        return connection
    }

    override fun destroyObject(poolObject: PooledObject<CuratorFramework>) {
        if(poolObject.`object`?.state!=CuratorFrameworkState.STOPPED){
            poolObject.`object`.close()
        }
    }

    override fun validateObject(poolObject: PooledObject<CuratorFramework>): Boolean {
        if(poolObject.`object`?.state==CuratorFrameworkState.STARTED){
            return true
        }
        return false
    }

    fun validateConnection(curatorFramework: CuratorFramework): Boolean {
        if(curatorFramework.state==CuratorFrameworkState.STARTED){
            return true
        }
        return false
    }

    override fun wrap(curatorFramework: CuratorFramework): PooledObject<CuratorFramework> {
        return DefaultPooledObject(curatorFramework)
    }

}