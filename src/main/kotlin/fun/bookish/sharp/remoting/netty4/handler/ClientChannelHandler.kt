package `fun`.bookish.sharp.remoting.netty4.handler

import `fun`.bookish.sharp.model.RpcResponse
import `fun`.bookish.sharp.proxy.RpcResponseManager
import io.netty.channel.*
import org.apache.log4j.Logger

/**
 * 客户端入站处理器,处理由服务器返回的Rpc响应消息
 */
@ChannelHandler.Sharable
class ClientChannelHandler: SimpleChannelInboundHandler<RpcResponse>() {

    private val logger: Logger = Logger.getLogger(this.javaClass)

    override fun channelActive(ctx: ChannelHandlerContext) {
    }

    override fun channelInactive(ctx: ChannelHandlerContext) {
    }

    override fun channelRead0(ctx: ChannelHandlerContext, response: RpcResponse) {
        if(response.success){
            RpcResponseManager.update(response.id, response.result!!)
        }else{
            RpcResponseManager.update(response.id, response.error!!)
        }
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
    }

}