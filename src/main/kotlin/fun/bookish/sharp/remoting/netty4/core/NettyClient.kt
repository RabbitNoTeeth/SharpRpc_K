package `fun`.bookish.sharp.remoting.netty4.core

import `fun`.bookish.sharp.config.ServiceReference
import `fun`.bookish.sharp.protocol.api.ProtocolName
import `fun`.bookish.sharp.proxy.ServiceProvidersLoader
import `fun`.bookish.sharp.remoting.netty4.codec.MessageCodec
import `fun`.bookish.sharp.remoting.netty4.handler.ClientChannelHandler
import `fun`.bookish.sharp.serialize.api.RpcSerializer
import `fun`.bookish.sharp.serialize.kryo.KryoSerializer
import `fun`.bookish.sharp.util.resolveAddress
import io.netty.bootstrap.Bootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import org.apache.log4j.Logger
import java.lang.IllegalStateException

/**
 * 客户端引导
 */
object NettyClient {

    private val logger: Logger = Logger.getLogger(this.javaClass)

    private val eventLoopGroup = NioEventLoopGroup()
    private val bootstrap = Bootstrap()

    private val rpcSerializer: RpcSerializer = KryoSerializer()

    init {
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel::class.java)
                //设置客户端连接的超时时间
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,10000)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(object : ChannelInitializer<SocketChannel>() {
                    override fun initChannel(channel: SocketChannel) {
                        channel.pipeline().addLast(MessageCodec(rpcSerializer))
                                .addLast(ClientChannelHandler())
                    }
                })
    }

    fun newChannel(serviceReference: ServiceReference<*>):Channel{

        val serviceName = serviceReference.serviceInterface.typeName
        val providers = ServiceProvidersLoader.getProviders(serviceReference).filter { it.protocol== ProtocolName.SHARP }.toMutableList()
        val channel:Channel? = null

        //按照权重进行连接
        for(x in providers.size-1 downTo 0 step 1) {
            val registerValue = providers[x]
            try {
                val channelFuture = this.bootstrap.clone().connect(resolveAddress(registerValue.address)).sync()
                        //添加监听器,判断连接是否成功,当连接超时时,进入if代码块,抛出异常
                        .addListener { future ->
                            if (future.isDone && !future.isSuccess) {
                                throw IllegalArgumentException()
                            }
                        }
                channelFuture.channel()
            } catch (e: Exception) {
                logger.warn("failed to connect to the provider \"[${registerValue.protocol.value}] ${registerValue.address}\" of service \"$serviceName\"")
            }
        }

        return channel?:throw IllegalStateException("there is no available provider of service \"$serviceName\"")

    }

    fun initChannel(serviceReference: ServiceReference<*>, address: String, directConnect: Boolean):Channel{

        val serviceName = serviceReference.serviceInterface.typeName

        return try {
            val channelFuture = this.bootstrap.clone().connect(resolveAddress(address)).sync()
                    .addListener { future ->
                        if (future.isDone && !future.isSuccess){
                            throw IllegalArgumentException()
                        }
                    }
            channelFuture.channel()
        }catch (e: Exception){
            if(directConnect){
                val message = "failed to connect to the provider \"[SHARP] $address\" of service \"$serviceName\""
                logger.error(message)
                throw IllegalStateException(message)
            }else{
                this.newChannel(serviceReference)
            }
        }

    }

}