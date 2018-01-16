package cn.booklish.sharp.remoting.netty4.core

import cn.booklish.sharp.remoting.netty4.config.ClientConfig
import cn.booklish.sharp.remoting.netty4.handler.ClientChannelInitializer
import cn.booklish.sharp.remoting.netty4.util.NettyUtil
import io.netty.bootstrap.Bootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioSocketChannel

/**
 * 客户端引导
 */
object Client {

    private val eventLoopGroup = NioEventLoopGroup()
    private val bootstrap = Bootstrap()

    fun init(clientConfig: ClientConfig){
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel::class.java)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(ClientChannelInitializer(clientConfig.channelOperator, clientConfig.rpcSerializer!!))
    }

    fun newChannel(address: String):Channel{
        return bootstrap.connect(NettyUtil.resolveAddressString(address)).sync().channel()
    }

}