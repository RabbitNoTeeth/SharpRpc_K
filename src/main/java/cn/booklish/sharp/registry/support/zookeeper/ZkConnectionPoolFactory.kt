package cn.booklish.sharp.registry.support.zookeeper

import org.apache.commons.pool2.impl.GenericObjectPool
import org.apache.curator.framework.CuratorFramework
import org.apache.curator.framework.imps.CuratorFrameworkState

/**
 * @Author: liuxindong
 * @Description:  zookeeper连接池工厂
 * @Created: 2017/12/20 14:43
 * @Modified:
 */
class ZkConnectionPoolFactory(zkConnectionConfig: ZkConnectionConfig) {

    private val connectionFactory = ZkConnectionFactory(zkConnectionConfig)

    private val pool = GenericObjectPool<CuratorFramework>(connectionFactory,zkConnectionConfig.poolConfig)

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