package cn.booklish.sharp.remoting.netty4.config

import cn.booklish.sharp.constant.SharpConstants
import cn.booklish.sharp.registry.api.RegistryCenter
import cn.booklish.sharp.remoting.netty4.api.ChannelOperator
import cn.booklish.sharp.serialize.api.RpcSerializer

/**
 * 客户端配置类
 */
class ClientConfig {

    var rpcSerializer: RpcSerializer? = null

    var channelOperator:ChannelOperator = SharpConstants.DEFAULT_CLIENT_CHANNEL_OPERATOR

    var registryCenter: RegistryCenter? = null

    fun rpcSerializer(rpcSerializer: RpcSerializer):ClientConfig{
        this.rpcSerializer = rpcSerializer
        return this
    }

    fun channelOperator(channelOperator: ChannelOperator):ClientConfig{
        this.channelOperator = channelOperator
        return this
    }

    fun registryCenter(registryCenter: RegistryCenter):ClientConfig{
        this.registryCenter = registryCenter
        return this
    }

}