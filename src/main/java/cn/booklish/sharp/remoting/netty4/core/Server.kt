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
import org.apache.log4j.Logger
import java.util.concurrent.Executors

/**
 * 服务端引导类
 */
class Server(private val serviceExport: ServiceExport<*>) {

    private val logger: Logger = Logger.getLogger(this.javaClass)

    private val bossGroup = NioEventLoopGroup()
    private val workerGroup = NioEventLoopGroup()
    private val bootstrap = ServerBootstrap()
    private val executor = Executors.newFixedThreadPool(serviceExport.protocols.size)

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

        val protocols = serviceExport.protocols

        for (protocol in protocols){
            executor.execute({
                try {
                    var port = protocol.port
                    //如果是RMI协议,那么server端默认绑定在protocol设置的端口+10000上
                    if(protocol.name==ProtocolName.RMI){
                        port += 10000
                    }
                    val f = this.bootstrap.bind(port).sync().addListener { future ->
                        if(future.isDone && future.isSuccess){
                            logger.info("成功启动服务 ${serviceExport.serviceInterface.typeName} 监听,端口:$port")
                        }
                    }
                    f.channel().closeFuture().sync()
                } catch (e: InterruptedException) {
                    Thread.currentThread().interrupt()
                } finally {
                    workerGroup.shutdownGracefully()
                    bossGroup.shutdownGracefully()
                }
            })
        }

    }

}