package cn.booklish.sharp.remoting.netty4.handler

import cn.booklish.sharp.remoting.netty4.api.ChannelOperator
import cn.booklish.sharp.remoting.netty4.codec.MessageCodec
import cn.booklish.sharp.serialize.api.RpcSerializer
import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.handler.timeout.ReadTimeoutHandler
import org.apache.log4j.Logger


/**
 * 客户端Channel初始化器,用于创建channel的pipeline链
 */
class ClientChannelInitializer(private val channelOperator: ChannelOperator,
                               private val rpcSerializer: RpcSerializer
): ChannelInitializer<SocketChannel>() {

    private val logger:Logger = Logger.getLogger(this.javaClass)

    override fun initChannel(socketChannel: SocketChannel) {
        val pipeline = socketChannel.pipeline()
        pipeline.addLast(MessageCodec(rpcSerializer))
                .addLast(ClientHandler(channelOperator))
    }

}

/**
 * 服务器Channel初始化器,用于创建channel的pipeline链
 */
class ServerChannelInitializer(private val channelOperator: ChannelOperator,
                               private val rpcSerializer: RpcSerializer
): ChannelInitializer<SocketChannel>() {

    private val logger:Logger = Logger.getLogger(this.javaClass)

    override fun initChannel(socketChannel: SocketChannel) {
        val pipeline = socketChannel.pipeline()
        pipeline.addLast(MessageCodec(rpcSerializer))
                .addLast(ServerHandler(channelOperator))
    }

}