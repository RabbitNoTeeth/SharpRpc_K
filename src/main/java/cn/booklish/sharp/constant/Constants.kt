package cn.booklish.sharp.constant


object Constants {

    /**
     * 默认zookeeper连接地址
     */
    val DEFAULT_ZOOKEEPER_ADDRESS = "127.0.0.1:2181"

    /**
     * 默认zookeeper连接池大小
     */
    val DEFAULT_ZOOKEEPER_CONNECTION_POOL_SIZE = 15

    /**
     * 默认zookeeper重连次数
     */
    val DEFAULT_ZOOKEEPER_RETRY_TIMES = 3

    /**
     * 默认zookeeper重连间隔时长
     */
    val DEFAULT_ZOOKEEPER_SLEEP_BETWEEN_RETRY = 3000

    /**
     * 默认Rpc请求异步处理
     */
    val DEFAULT_RPC_REQUEST_COMPUTE_MANAGER_ASYNC = true

    /**
     * 默认Rpc异步处理线程池大小
     */
    val DEFAULT_RPC_REQUEST_COMPUTE_MANAGER_THREAD_POOL_SIZE = 2

    /**
     * 默认Rpc注册管理器线程池大小
     */
    val DEFAULT_REGISTER_TASK_MANAGER_THREAD_POOL_SIZE = 2

    /**
     * 默认server监听端口
     */
    val DEFAULT_SERVER_LISTEN_PORT = 12200

    /**
     * 默认客户端rpc请求连接过期时间
     */
    val DEFAULT_CLIENT_CHANNEL_TIMEOUT = 45

}