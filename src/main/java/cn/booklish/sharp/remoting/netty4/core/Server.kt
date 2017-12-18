package cn.booklish.sharp.remoting.netty4.core

import cn.booklish.sharp.constant.Constants
import cn.booklish.sharp.remoting.netty4.api.ServerChannelInOperator
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import java.util.concurrent.Executors


/**
 * @Author: liuxindong
 * @Description:  Rpc服务器引导
 * @Created: 2017/12/13 9:04
 * @Modified:
 */
object RpcServerBootStrap {

    private val bossGroup = NioEventLoopGroup()
    private val workerGroup = NioEventLoopGroup()
    private lateinit var channel: Channel
    private val executor = Executors.newSingleThreadExecutor()
    private val bootstrap = ServerBootstrap()
    private var port = Constants.DEFAULT_SERVER_LISTEN_PORT
    private var clientChannelTimeout = Constants.DEFAULT_CLIENT_CHANNEL_TIMEOUT

    /**
     * 默认配置并启动
     */
    fun init(port:Int?,clientChannelTimeout:Int?):RpcServerBootStrap{
        port?.let { this.port = it }
        clientChannelTimeout?.let { this.clientChannelTimeout = it }
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel::class.java)
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(ServerChannelInitializer(this.clientChannelTimeout,ServerChannelInOperator()))
        return this
    }

    /**
     * 启动Rpc服务器
     */
    fun start() {

        executor.execute(ServerStartTask(this.bootstrap, this.port))

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


    /**
     * 服务启动runnable类
     */
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