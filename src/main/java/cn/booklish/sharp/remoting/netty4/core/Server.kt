package cn.booklish.sharp.remoting.netty4.core

import cn.booklish.sharp.constant.SharpConstants
import cn.booklish.sharp.remoting.netty4.api.ChannelOperator
import cn.booklish.sharp.remoting.netty4.api.ServerChannelOperator
import cn.booklish.sharp.serialize.api.RpcSerializer
import cn.booklish.sharp.serialize.kryo.KryoSerializer
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import java.util.concurrent.Executors

/**
 * @Author: liuxindong
 * @Description: 服务端引导类
 * @Create: don9 2017/12/18
 * @Modify:
 */
object Server {

    private val bossGroup = NioEventLoopGroup()
    private val workerGroup = NioEventLoopGroup()
    private lateinit var channel: Channel
    private val executor = Executors.newSingleThreadExecutor()
    private val bootstrap = ServerBootstrap()
    var port = SharpConstants.DEFAULT_SERVER_LISTEN_PORT
    var clientChannelTimeout = SharpConstants.DEFAULT_CLIENT_CHANNEL_TIMEOUT
    var rpcSerializer: RpcSerializer = KryoSerializer()
    var channelOperator: ChannelOperator = ServerChannelOperator()

    /**
     * 默认配置并启动
     */
    fun init():Server{
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel::class.java)
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(ServerChannelInitializer(this.clientChannelTimeout, this.channelOperator,this.rpcSerializer))
        return this
    }

    /**
     * 启动Rpc服务器
     */
    fun start() {

        executor.execute({
            try {
                val f = this.bootstrap.bind(this.port).sync()
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