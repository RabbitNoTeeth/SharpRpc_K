package cn.booklish.sharp.remoting.netty4.config

import cn.booklish.sharp.constant.SharpConstants
import cn.booklish.sharp.registry.api.RegistryCenter
import cn.booklish.sharp.serialize.api.RpcSerializer

/**
 * 服务端配置类
 */
class ServerConfig {

    var listenPort = SharpConstants.DEFAULT_PROTOCOL_PORT

    var computeThreadPoolSize = SharpConstants.DEFAULT_SERVER_COMPUTE_THREAD_POOL_SIZE

    var rpcSerializer: RpcSerializer? = null

    var channelOperator = SharpConstants.DEFAULT_SERVER_CHANNEL_OPERATOR

    var registryCenter:RegistryCenter? = null

}