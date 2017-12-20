package cn.booklish.sharp.remoting.netty4.core

import cn.booklish.sharp.remoting.netty4.api.ChannelOperator
import cn.booklish.sharp.remoting.netty4.api.ClientChannelOperator
import cn.booklish.sharp.serialize.api.RpcSerializer
import cn.booklish.sharp.serialize.kryo.KryoSerializer
import io.netty.bootstrap.Bootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioSocketChannel
import org.apache.log4j.Logger
import java.net.InetSocketAddress
import java.util.concurrent.Semaphore

/**
 * @Author: liuxindong
 * @Description:  客户端引导
 * @Created: 2017/12/20 9:44
 * @Modified:
 */
object Client {

    private val eventLoopGroup = NioEventLoopGroup()
    private val bootstrap = Bootstrap()

    var rpcSerializer: RpcSerializer = KryoSerializer()
    var channelOperator: ChannelOperator = ClientChannelOperator()

    fun init(){
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel::class.java)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(ClientChannelInitializer(this.channelOperator,this.rpcSerializer))
    }

    fun newChannel(address: InetSocketAddress):Channel{
        return bootstrap.connect(address).sync().channel()
    }

}