package cn.booklish.sharp.remoting.netty4.core

import cn.booklish.sharp.constant.SharpConstants
import cn.booklish.sharp.registry.api.RegistryCenter
import cn.booklish.sharp.remoting.netty4.api.ChannelOperator
import cn.booklish.sharp.remoting.netty4.api.ClientChannelOperator
import cn.booklish.sharp.serialize.api.RpcSerializer
import cn.booklish.sharp.serialize.kryo.KryoSerializer
import org.apache.commons.pool2.impl.GenericObjectPoolConfig

/**
 * @Author: liuxindong
 * @Description:  客户端配置类
 * @Created: 2017/12/22 8:41
 * @Modified:
 */
class ClientConfig {

    var channelTimeout = SharpConstants.DEFAULT_CLIENT_CHANNEL_TIMEOUT

    var rpcSerializer: RpcSerializer = KryoSerializer()

    var channelOperator: ChannelOperator = ClientChannelOperator()

    var channelPoolConfig = GenericObjectPoolConfig()

    var registryCenter: RegistryCenter? = null

    init {
        channelPoolConfig.testOnReturn = true
        channelPoolConfig.testOnBorrow = true
    }

}