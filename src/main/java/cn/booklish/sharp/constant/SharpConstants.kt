package cn.booklish.sharp.constant

import kotlin.math.min

/**
 * @Author: liuxindong
 * @Description:  Sharp配置默认值常量
 * @Created: 2017/12/20 9:39
 * @Modified:
 */
object SharpConstants {

    /*--------------------------默认zookeeper配置--------------------------*/

    /**
     * 默认zookeeper连接地址
     */
    val DEFAULT_ZOOKEEPER_ADDRESS = "127.0.0.1:2181"

    /**
     * 默认zookeeper重连次数
     */
    val DEFAULT_ZOOKEEPER_RETRY_TIMES = 3

    /**
     * 默认zookeeper重连间隔时长
     */
    val DEFAULT_ZOOKEEPER_SLEEP_BETWEEN_RETRY = 3000

    /**
     * 默认zookeeper会话超时时长
     */
    val DEFAULT_ZOOKEEPER_SESSION_TIMEOUT = 60000

    /**
     * 默认zookeeper连接超时时长
     */
    val DEFAULT_ZOOKEEPER_CONNECTION_TIMEOUT = 15000

    /*--------------------------默认redis配置--------------------------*/

    /**
     * 默认redis主机地址
     */
    val DEFAULT_REDIS_ADDRESS = "127.0.0.1:6379"

    /**
     * 默认redis连接超时时长
     */
    val DEFAULT_REDIS_CONNECTION_TIMEOUT = 2000

    /*--------------------------默认server服务端配置--------------------------*/

    /**
     * 默认服务端server监听端口
     */
    val DEFAULT_SERVER_LISTEN_PORT = 12200

    /**
     * 默认服务注册地址前缀
     */
    val DEFAULT_REGISTER_PATH_PREFIX = "sharp-rpc:"

    /**
     * 默认Rpc请求异步处理
     */
    val DEFAULT_RPC_REQUEST_COMPUTE_MANAGER_ASYNC = true

    /**
     * 默认Rpc异步处理线程池大小
     */
    val DEFAULT_RPC_REQUEST_COMPUTE_MANAGER_THREAD_POOL_SIZE = min(Runtime.getRuntime().availableProcessors()+1,32)

    /**
     * 默认Rpc注册管理器线程池大小
     */
    val DEFAULT_REGISTER_TASK_MANAGER_THREAD_POOL_SIZE = min(Runtime.getRuntime().availableProcessors()+1,32)

    /*--------------------------默认client客户端配置--------------------------*/

    /**
     * 默认客户端rpc请求连接过期时间
     */
    val DEFAULT_CLIENT_CHANNEL_TIMEOUT = 60


}