package cn.booklish.sharp.remoting.netty4.core

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

    /**
     * 默认配置并启动
     */
    fun init(serverConfig: ServerConfig):Server{
        this.serverConfig = serverConfig
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
                val f = this.bootstrap.bind(serverConfig.listenPort).sync()
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