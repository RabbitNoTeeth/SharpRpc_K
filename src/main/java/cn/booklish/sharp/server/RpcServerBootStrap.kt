package cn.booklish.sharp.server

import cn.booklish.sharp.pipeline.DefaultServerChannelInitializer
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import java.util.concurrent.Executors


/**
 * Rpc服务器引导
 */
object RpcServerBootStrap {

    private val bossGroup = NioEventLoopGroup()
    private val workerGroup = NioEventLoopGroup()
    private var channel: Channel? = null
    private val executor = Executors.newSingleThreadExecutor()
    val b = ServerBootstrap()

    /**
     * 默认配置并启动
     */
    fun defaultConfigureAndStart(port:Int = 12200,clientChannelTimeout:Int = 40){
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel::class.java)
                .childHandler(DefaultServerChannelInitializer(clientChannelTimeout))
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true)

        start(b,port)
    }

    /**
     * 启动Rpc服务器
     */
    fun start(bootstrap: ServerBootstrap,port:Int = 12200) {

        executor.execute(ServerStartTask(bootstrap, port))

    }

    /**
     * 停止服务器
     */
    fun stop() {
        channel!!.closeFuture().syncUninterruptibly()
        executor.shutdown()
        workerGroup.shutdownGracefully()
        bossGroup.shutdownGracefully()
    }


    private class ServerStartTask(val serverBootstrap: ServerBootstrap, val port: Int) : Runnable {

        override fun run() {
            try {
                val f = serverBootstrap.bind(port).sync()
                channel = f.channel()
                f.channel().closeFuture().sync()
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
            } finally {
                workerGroup.shutdownGracefully()
                bossGroup.shutdownGracefully()
            }
        }
    }


}