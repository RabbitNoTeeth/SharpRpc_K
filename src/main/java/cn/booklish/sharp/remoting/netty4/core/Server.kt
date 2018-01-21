package cn.booklish.sharp.remoting.netty4.core

import cn.booklish.sharp.config.ServiceExport
import cn.booklish.sharp.constant.SharpConstants
import cn.booklish.sharp.protocol.api.ProtocolName
import cn.booklish.sharp.remoting.netty4.handler.ServerChannelInitializer
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import java.util.concurrent.Executors

/**
 * 服务端引导类
 */
class Server(private val serviceExport: ServiceExport<*>) {

    private val bossGroup = NioEventLoopGroup()
    private val workerGroup = NioEventLoopGroup()
    private val bootstrap = ServerBootstrap()
    private val executor = Executors.newSingleThreadExecutor()

    private lateinit var channel: Channel

    init {
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel::class.java)
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(ServerChannelInitializer(SharpConstants.DEFAULT_SERVER_CHANNEL_OPERATOR, SharpConstants.DEFAULT_SERIALIZER))
    }

    /**
     * 启动Rpc服务器
     */
    fun start() {

        executor.execute({
            try {
                val protocols = serviceExport.protocols
                for (protocol in protocols){
                    var port = protocol.port
                    //如果是RMI协议,那么server端默认绑定在protocol设置的端口+10000上
                    if(protocol.name==ProtocolName.RMI){
                        port += 10000
                    }
                    val f = this.bootstrap.bind(port).sync()
                    channel = f.channel()
                    f.channel().closeFuture().sync()
                }
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
            } finally {
                workerGroup.shutdownGracefully()
                bossGroup.shutdownGracefully()
            }
        })

    }

}