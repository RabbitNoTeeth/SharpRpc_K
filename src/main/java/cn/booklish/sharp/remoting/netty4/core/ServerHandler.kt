package cn.booklish.sharp.remoting.netty4.core

import cn.booklish.sharp.remoting.netty4.api.ChannelOperator
import cn.booklish.sharp.remoting.netty4.util.NettyChannelUtil
import io.netty.channel.*
import java.util.concurrent.ConcurrentHashMap

/**
 * @Author: liuxindong
 * @Description:  服务器channel处理器
 * @Created: 2017/12/13 8:56
 * @Modified:
 */
@ChannelHandler.Sharable
class ServerHandler(private val channelOperator: ChannelOperator) : ChannelDuplexHandler(){

    private val map:ConcurrentHashMap<String, Channel> = ConcurrentHashMap()

    override fun channelActive(ctx: ChannelHandlerContext) {
        ctx.fireChannelActive()
        map.putIfAbsent(NettyChannelUtil.getRemoteAddressAsString(ctx.channel()),ctx.channel())
        channelOperator.connected(ctx.channel())
    }

    override fun channelInactive(ctx: ChannelHandlerContext) {
        map.remove(NettyChannelUtil.getRemoteAddressAsString(ctx.channel()),ctx.channel())
        channelOperator.disconnected(ctx.channel())
    }

    override fun channelRead(ctx: ChannelHandlerContext, message: Any) {
        channelOperator.receive(ctx.channel(),message)
    }

    override fun write(ctx: ChannelHandlerContext, msg: Any, promise: ChannelPromise) {
        channelOperator.send(ctx.channel(),msg)
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        channelOperator.caught(ctx.channel(),cause)
    }

    fun getChannels():Map<String, Channel>{
        return map
    }

}