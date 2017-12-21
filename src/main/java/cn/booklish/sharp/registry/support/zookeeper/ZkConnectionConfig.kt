package cn.booklish.sharp.registry.support.zookeeper

import cn.booklish.sharp.constant.SharpConstants
import org.apache.commons.pool2.impl.GenericObjectPoolConfig

/**
 * @Author: liuxindong
 * @Description:  zookeeper连接配置类
 * @Created: 2017/12/21 14:13
 * @Modified:
 */
class ZkConnectionConfig {

    var address = SharpConstants.DEFAULT_ZOOKEEPER_ADDRESS

    var retryTimes = SharpConstants.DEFAULT_ZOOKEEPER_RETRY_TIMES

    var sleepBetweenRetry = SharpConstants.DEFAULT_ZOOKEEPER_SLEEP_BETWEEN_RETRY

    var sessionTimeout = SharpConstants.DEFAULT_ZOOKEEPER_SESSION_TIMEOUT

    var connectionTimeOut = SharpConstants.DEFAULT_ZOOKEEPER_CONNECTION_TIMEOUT

    val poolConfig = GenericObjectPoolConfig()

    init {
        poolConfig.testOnBorrow = true
        poolConfig.testOnReturn = true
    }
}