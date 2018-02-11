package `fun`.bookish.sharp.remoting.netty4.handler

import `fun`.bookish.sharp.compute.ComputeTaskManager
import `fun`.bookish.sharp.manage.state.ProviderStateManager
import `fun`.bookish.sharp.model.RpcRequest
import `fun`.bookish.sharp.util.resolveAddress
import io.netty.channel.*
import org.apache.log4j.Logger
import java.net.InetSocketAddress

/**
 * 服务器channel处理器
 */
@ChannelHandler.Sharable
class ServerChannelHandler : SimpleChannelInboundHandler<RpcRequest>(){

    private val logger: Logger = Logger.getLogger(this.javaClass)

    override fun channelActive(ctx: ChannelHandlerContext) {
        val localAddress = ctx.channel().localAddress()
        val remoteAddress = ctx.channel().remoteAddress()
        ProviderStateManager.incConCount(resolveAddress(localAddress as InetSocketAddress), resolveAddress(remoteAddress as InetSocketAddress))
    }

    override fun channelInactive(ctx: ChannelHandlerContext) {
        val localAddress = ctx.channel().localAddress()
        val remoteAddress = ctx.channel().remoteAddress()
        ProviderStateManager.incConCount(resolveAddress(localAddress as InetSocketAddress), resolveAddress(remoteAddress as InetSocketAddress))
    }

    override fun channelRead0(ctx: ChannelHandlerContext, request: RpcRequest) {
        logger.info("receive a rpc request from ${ctx.channel().remoteAddress()}, start compute this request")
        val computeResult = ComputeTaskManager.submit(request).get()
        logger.info("successfully compute the request from ${ctx.channel().remoteAddress()}, send the result to the client")
        ctx.channel().writeAndFlush(computeResult)
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
    }

}