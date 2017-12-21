package cn.booklish.sharp.registry.support.zookeeper

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
class ZkConnectionFactory(private val zkConnectionConfig: ZkConnectionConfig): BasePooledObjectFactory<CuratorFramework>() {

    override fun create(): CuratorFramework {
        val connection = CuratorFrameworkFactory.newClient(zkConnectionConfig.address,
                zkConnectionConfig.sessionTimeout,zkConnectionConfig.connectionTimeOut,
                RetryNTimes(zkConnectionConfig.retryTimes, zkConnectionConfig.sleepBetweenRetry))
        connection.start()
        return connection
    }

    override fun destroyObject(poolObject: PooledObject<CuratorFramework>) {
        if(poolObject.`object`?.state!=CuratorFrameworkState.STOPPED){
            poolObject.`object`.close()
        }
    }

    override fun validateObject(poolObject: PooledObject<CuratorFramework>): Boolean {
        return poolObject.`object`?.state==CuratorFrameworkState.STARTED
    }

    override fun wrap(curatorFramework: CuratorFramework): PooledObject<CuratorFramework> {
        return DefaultPooledObject(curatorFramework)
    }

}