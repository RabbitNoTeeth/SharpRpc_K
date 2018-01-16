package cn.booklish.sharp.remoting.netty4.handler

import cn.booklish.sharp.remoting.netty4.api.ChannelOperator
import cn.booklish.sharp.remoting.netty4.util.NettyUtil
import io.netty.channel.*
import java.util.concurrent.ConcurrentHashMap

/**
 * 服务器channel处理器
 */
@ChannelHandler.Sharable
class ServerHandler(private val channelOperator: ChannelOperator) : ChannelDuplexHandler(){

    private val map:ConcurrentHashMap<String, Channel> = ConcurrentHashMap()

    override fun channelActive(ctx: ChannelHandlerContext) {
        ctx.fireChannelActive()
        map.putIfAbsent(NettyUtil.getRemoteAddressAsString(ctx.channel()),ctx.channel())
        channelOperator.connected(ctx.channel())
    }

    override fun channelInactive(ctx: ChannelHandlerContext) {
        map.remove(NettyUtil.getRemoteAddressAsString(ctx.channel()),ctx.channel())
        channelOperator.disconnected(ctx.channel())
    }

    override fun channelRead(ctx: ChannelHandlerContext, message: Any) {
        channelOperator.receive(ctx.channel(),message)
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        channelOperator.caught(ctx.channel(),cause)
    }

    fun getChannels():Map<String, Channel>{
        return map
    }

}