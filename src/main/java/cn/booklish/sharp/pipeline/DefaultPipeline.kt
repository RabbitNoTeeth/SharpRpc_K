package cn.booklish.sharp.pipeline

import cn.booklish.sharp.codec.RpcMessageCodec
import cn.booklish.sharp.handler.DefaultClientChannelInboundHandler
import cn.booklish.sharp.handler.DefaultServerChannelInboundHandler
import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.handler.timeout.ReadTimeoutHandler
import org.apache.log4j.Logger


/**
 * @Author: liuxindong
 * @Description:  客户端Channel初始化器,用于创建channel的pipeline链
 * @Created: 2017/12/13 8:57
 * @Modified:
 */
class DefaultClientChannelInitializer: ChannelInitializer<SocketChannel>() {

    private val logger:Logger = Logger.getLogger(this.javaClass)

    override fun initChannel(socketChannel: SocketChannel) {
        val pipeline = socketChannel.pipeline()
        pipeline.addLast(RpcMessageCodec())
                .addLast(DefaultClientChannelInboundHandler())
        logger.info("[SharpRpc-client]: 客户端Channel处理器链Pipeline创建完成")
    }

}

/**
 * @Author: liuxindong
 * @Description:  服务器Channel初始化器,用于创建channel的pipeline链
 * @Created: 2017/12/13 8:57
 * @Modified:
 */
class DefaultServerChannelInitializer(private val clientChannelTimeout: Int = 40): ChannelInitializer<SocketChannel>() {

    private val logger:Logger = Logger.getLogger(this.javaClass)

    override fun initChannel(socketChannel: SocketChannel) {
        val pipeline = socketChannel.pipeline()
        pipeline.addLast(RpcMessageCodec())
                .addLast(ReadTimeoutHandler(clientChannelTimeout))
                .addLast(DefaultServerChannelInboundHandler())
        logger.info("[SharpRpc-server]: 服务器Channel处理器链Pipeline创建完成")
    }

}