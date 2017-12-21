package cn.booklish.sharp.registry.support.redis

import cn.booklish.sharp.constant.Constants
import org.apache.commons.pool2.impl.GenericObjectPoolConfig

/**
 * @Author: liuxindong
 * @Description:  redis连接配置类
 * @Created: 2017/12/21 14:13
 * @Modified:
 */
class RedisConnectionConfig {

    var address = Constants.DEFAULT_REDIS_ADDRESS

    var connectionTimeOut = Constants.DEFAULT_REDIS_CONNECTION_TIMEOUT

    val poolConfig = GenericObjectPoolConfig()

    init {
        poolConfig.testOnBorrow = true
        poolConfig.testOnReturn = true
    }
}