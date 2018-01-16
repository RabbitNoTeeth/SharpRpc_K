package cn.booklish.sharp.remoting.netty4.handler

import cn.booklish.sharp.remoting.netty4.api.ChannelOperator
import cn.booklish.sharp.remoting.netty4.util.NettyUtil
import io.netty.channel.*
import java.util.concurrent.ConcurrentHashMap

/**
 * 客户端入站处理器,处理由服务器返回的Rpc响应消息
 */
@ChannelHandler.Sharable
class ClientHandler(private val channelOperator: ChannelOperator): ChannelDuplexHandler() {

    private val map: ConcurrentHashMap<String, HashSet<Channel>> = ConcurrentHashMap()

    override fun channelActive(ctx: ChannelHandlerContext) {
        ctx.fireChannelActive()
        map.putIfAbsent(NettyUtil.getRemoteAddressAsString(ctx.channel()),hashSetOf(ctx.channel()))
        channelOperator.connected(ctx.channel())
    }

    override fun channelInactive(ctx: ChannelHandlerContext) {
        ctx.fireChannelInactive()
        map[NettyUtil.getRemoteAddressAsString(ctx.channel())]?.remove(ctx.channel())
        channelOperator.disconnected(ctx.channel())
    }

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        channelOperator.receive(ctx.channel(),msg)
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        channelOperator.caught(ctx.channel(),cause)
    }

    fun getChannels(): ConcurrentHashMap<String, HashSet<Channel>> {
        return map
    }

}