package cn.booklish.sharp.remoting.netty4.config

import cn.booklish.sharp.constant.SharpConstants
import cn.booklish.sharp.registry.api.RegistryCenter
import cn.booklish.sharp.serialize.api.RpcSerializer

/**
 * 客户端配置类
 */
class ClientConfig {

    var rpcSerializer: RpcSerializer? = null

    var channelOperator = SharpConstants.DEFAULT_CLIENT_CHANNEL_OPERATOR

    var registryCenter: RegistryCenter? = null

}