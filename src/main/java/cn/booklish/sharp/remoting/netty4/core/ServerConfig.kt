package cn.booklish.sharp.remoting.netty4.core

import cn.booklish.sharp.constant.SharpConstants
import cn.booklish.sharp.registry.api.RegistryCenter
import cn.booklish.sharp.remoting.netty4.api.ChannelOperator
import cn.booklish.sharp.remoting.netty4.api.ServerChannelOperator
import cn.booklish.sharp.serialize.api.RpcSerializer
import cn.booklish.sharp.serialize.kryo.KryoSerializer

/**
 * @Author: liuxindong
 * @Description:  服务端配置类
 * @Created: 2017/12/22 8:46
 * @Modified:
 */
class ServerConfig {

    var listenPort = SharpConstants.DEFAULT_SERVER_LISTEN_PORT

    var serverEnable = SharpConstants.DEFAULT_SERVER_ENABLE

    var autoScannerEnable = SharpConstants.DEFAULT__AUTO_SCAN_ENABLE

    var asyncComputeRpcRequest = SharpConstants.DEFAULT_RPC_REQUEST_COMPUTE_MANAGER_ASYNC

    var computeThreadPoolSize = SharpConstants.DEFAULT_RPC_REQUEST_COMPUTE_MANAGER_THREAD_POOL_SIZE

    var registerThreadPoolSize = SharpConstants.DEFAULT_REGISTER_TASK_MANAGER_THREAD_POOL_SIZE

    var clientChannelTimeout = SharpConstants.DEFAULT_CLIENT_CHANNEL_TIMEOUT

    var autoScanBasePath:String? = null

    var autoRegisterPath:String? = null

    var registryCenter:RegistryCenter? = null

    var channelOperator:ChannelOperator = ServerChannelOperator()

    var rpcSerializer: RpcSerializer = KryoSerializer()

}