package cn.booklish.sharp.remoting.netty4.core

import cn.booklish.sharp.remoting.netty4.api.ChannelOperator
import cn.booklish.sharp.remoting.netty4.util.NettyChannelUtil
import io.netty.channel.*
import java.util.concurrent.ConcurrentHashMap

/**
 * @Author: liuxindong
 * @Description:  默认的客户端入站处理器,处理由服务器返回的Rpc响应消息
 * @Created: 2017/12/13 8:55
 * @Modified:
 */
@ChannelHandler.Sharable
class ClientHandler(private val channelOperator: ChannelOperator): ChannelDuplexHandler() {

    private val map: ConcurrentHashMap<String, HashSet<Channel>> = ConcurrentHashMap()

    override fun channelActive(ctx: ChannelHandlerContext) {
        ctx.fireChannelActive()
        map.putIfAbsent(NettyChannelUtil.getRemoteAddressAsString(ctx.channel()),hashSetOf(ctx.channel()))
        channelOperator.connected(ctx.channel())
    }

    override fun channelInactive(ctx: ChannelHandlerContext) {
        ctx.fireChannelInactive()
        map[NettyChannelUtil.getRemoteAddressAsString(ctx.channel())]?.remove(ctx.channel())
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