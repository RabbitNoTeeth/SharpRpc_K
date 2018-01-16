package cn.booklish.sharp.remoting.netty4.config

import cn.booklish.sharp.constant.SharpConstants
import cn.booklish.sharp.registry.api.RegistryCenter
import cn.booklish.sharp.remoting.netty4.api.ChannelOperator
import cn.booklish.sharp.serialize.api.RpcSerializer

/**
 * 服务端配置类
 */
class ServerConfig {

    var computeThreadPoolSize = SharpConstants.DEFAULT_SERVER_COMPUTE_THREAD_POOL_SIZE

    var rpcSerializer: RpcSerializer? = null

    var channelOperator:ChannelOperator = SharpConstants.DEFAULT_SERVER_CHANNEL_OPERATOR

    var registryCenter:RegistryCenter? = null

    fun computeThreadPoolSize(computeThreadPoolSize: Int):ServerConfig{
        this.computeThreadPoolSize = computeThreadPoolSize
        return this
    }

    fun rpcSerializer(rpcSerializer: RpcSerializer):ServerConfig{
        this.rpcSerializer = rpcSerializer
        return this
    }

    fun channelOperator(channelOperator: ChannelOperator):ServerConfig{
        this.channelOperator = channelOperator
        return this
    }

    fun registryCenter(registryCenter: RegistryCenter):ServerConfig{
        this.registryCenter = registryCenter
        return this
    }

}