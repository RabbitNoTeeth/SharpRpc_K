package cn.booklish.sharp.registry.zookeeper

import org.apache.commons.pool2.impl.GenericObjectPool
import org.apache.commons.pool2.impl.GenericObjectPoolConfig
import org.apache.curator.framework.CuratorFramework
import org.apache.curator.framework.imps.CuratorFrameworkState

/**
 * @Author: liuxindong
 * @Description:  zookeeper连接池工厂
 * @Created: 2017/12/20 14:43
 * @Modified:
 */
class ZkConnectionPoolFactory(zkAddress:String,zkRetryTimes:Int,zkSleepBetweenRetry:Int,config:GenericObjectPoolConfig) {

    private val connectionFactory = ZkConnectionFactory(zkAddress,zkRetryTimes,zkSleepBetweenRetry)

    private val pool = GenericObjectPool<CuratorFramework>(connectionFactory,config)

    fun getConnection():CuratorFramework{
        return pool.borrowObject()
    }

    fun releaseConnection(curatorFramework: CuratorFramework){
        try {
            pool.returnObject(curatorFramework)
        }catch (e:Exception){
            if(curatorFramework.state == CuratorFrameworkState.STARTED){
                curatorFramework.close()
            }
        }
    }

}