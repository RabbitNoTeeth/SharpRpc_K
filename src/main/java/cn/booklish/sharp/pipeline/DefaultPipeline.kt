package cn.booklish.sharp.pipeline

import cn.booklish.sharp.codec.RpcMessageCodec
import cn.booklish.sharp.handler.DefaultClientChannelInboundHandler
import cn.booklish.sharp.handler.DefaultServerChannelInboundHandler
import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.handler.timeout.ReadTimeoutHandler
import org.apache.log4j.Logger


/**
 * 客户端pipeline
 */
class DefaultClientChannelInitializer: ChannelInitializer<SocketChannel>() {

    val logger:Logger = Logger.getLogger(this.javaClass)

    override fun initChannel(socketChannel: SocketChannel) {
        val pipeline = socketChannel.pipeline()
        pipeline.addLast(RpcMessageCodec())
                .addLast(DefaultClientChannelInboundHandler())

        logger.info("[SharpRpc-client]: 客户端Channel处理器链Pipeline创建完成")
    }

}

/**
 * 服务器pipeline
 */
class DefaultServerChannelInitializer(val clientChannelTimeout: Int = 40): ChannelInitializer<SocketChannel>() {

    val logger:Logger = Logger.getLogger(this.javaClass)

    override fun initChannel(socketChannel: SocketChannel) {
        val pipeline = socketChannel.pipeline()
        pipeline.addLast(RpcMessageCodec())
                .addLast(ReadTimeoutHandler(clientChannelTimeout))
                .addLast(DefaultServerChannelInboundHandler())
        logger.info("[SharpRpc-server]: 服务器Channel处理器链Pipeline创建完成")
    }

}