package cn.booklish.sharp.remoting.netty4.core

import io.netty.bootstrap.Bootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioSocketChannel
import java.net.InetSocketAddress

/**
 * @Author: liuxindong
 * @Description:  客户端引导
 * @Created: 2017/12/20 9:44
 * @Modified:
 */
object Client {

    private val eventLoopGroup = NioEventLoopGroup()
    private val bootstrap = Bootstrap()

    fun init(clientConfig: ClientConfig){
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel::class.java)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(ClientChannelInitializer(clientConfig.channelOperator,clientConfig.rpcSerializer))
    }

    fun newChannel(address: InetSocketAddress):Channel{
        return bootstrap.connect(address).sync().channel()
    }

}