package cn.booklish.sharp.remoting.netty4.core

import cn.booklish.sharp.remoting.netty4.api.ChannelOperator
import cn.booklish.sharp.serialize.api.RpcSerializer
import cn.booklish.sharp.serialize.kryo.KryoSerializer
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
class ClientChannelInitializer(private val channelOperator: ChannelOperator,
                               private val rpcSerializer: RpcSerializer
): ChannelInitializer<SocketChannel>() {

    private val logger:Logger = Logger.getLogger(this.javaClass)

    override fun initChannel(socketChannel: SocketChannel) {
        val pipeline = socketChannel.pipeline()
        pipeline//.addLast(Codec())
                .addLast(MessageDecoder(rpcSerializer))
                .addLast(MessageEncoder(rpcSerializer))
                .addLast(ClientHandler(channelOperator))
        logger.info("[SharpRpc-client]: 客户端Channel处理器链Pipeline创建完成")
    }

}

/**
 * @Author: liuxindong
 * @Description:  服务器Channel初始化器,用于创建channel的pipeline链
 * @Created: 2017/12/13 8:57
 * @Modified:
 */
class ServerChannelInitializer(private val clientChannelTimeout: Int = 40,
                               private val channelOperator: ChannelOperator,
                               private val rpcSerializer: RpcSerializer
): ChannelInitializer<SocketChannel>() {

    private val logger:Logger = Logger.getLogger(this.javaClass)

    override fun initChannel(socketChannel: SocketChannel) {
        val pipeline = socketChannel.pipeline()
        pipeline//.addLast(Codec())
                .addLast(MessageDecoder(rpcSerializer))
                .addLast(MessageEncoder(rpcSerializer))
                .addLast(ReadTimeoutHandler(clientChannelTimeout))
                .addLast(ServerHandler(channelOperator))
        logger.info("[SharpRpc-server]: 服务器Channel处理器链Pipeline创建完成")
    }

}