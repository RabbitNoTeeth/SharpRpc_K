package cn.booklish.sharp.remoting.netty4.core

import cn.booklish.sharp.protocol.api.ProtocolName
import cn.booklish.sharp.protocol.config.ProtocolConfig
import cn.booklish.sharp.remoting.netty4.config.ServerConfig
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
object Server {

    private val bossGroup = NioEventLoopGroup()
    private val workerGroup = NioEventLoopGroup()
    private lateinit var channel: Channel
    private val executor = Executors.newSingleThreadExecutor()
    private val bootstrap = ServerBootstrap()
    private lateinit var serverConfig: ServerConfig
    private lateinit var protocolConfig: ProtocolConfig

    /**
     * 默认配置并启动
     */
    fun init(serverConfig: ServerConfig,protocolConfig: ProtocolConfig):Server{
        this.serverConfig = serverConfig
        this.protocolConfig = protocolConfig
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel::class.java)
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(ServerChannelInitializer(serverConfig.channelOperator, serverConfig.rpcSerializer!!))
        return this
    }

    /**
     * 启动Rpc服务器
     */
    fun start() {

        executor.execute({
            try {
                var port = protocolConfig.port
                //如果是RMI协议,那么server端默认绑定在protocol设置的端口+10000上
                if(protocolConfig.name==ProtocolName.RMI){
                    port += 10000
                }
                val f = this.bootstrap.bind(port).sync()
                channel = f.channel()
                f.channel().closeFuture().sync()
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
            } finally {
                workerGroup.shutdownGracefully()
                bossGroup.shutdownGracefully()
            }
        })

    }

    /**
     * 停止服务器
     */
    fun stop() {
        channel.closeFuture().syncUninterruptibly()
        executor.shutdown()
        workerGroup.shutdownGracefully()
        bossGroup.shutdownGracefully()
    }

}