package cn.booklish.sharp.remoting.netty4.core

import cn.booklish.sharp.config.ServiceReference
import cn.booklish.sharp.constant.SharpConstants
import cn.booklish.sharp.protocol.api.ProtocolName
import cn.booklish.sharp.proxy.ProvidersLoader
import cn.booklish.sharp.proxy.ProxyServiceInterceptor
import cn.booklish.sharp.proxy.ServiceProxyFactory
import cn.booklish.sharp.remoting.netty4.handler.ClientChannelInitializer
import cn.booklish.sharp.remoting.netty4.util.NettyUtil
import io.netty.bootstrap.Bootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioSocketChannel
import net.sf.cglib.proxy.Enhancer
import org.apache.log4j.Logger
import java.lang.IllegalStateException
import java.rmi.Naming
import java.util.*

/**
 * 客户端引导
 */
object NettyClient {

    private val logger: Logger = Logger.getLogger(this.javaClass)

    private val eventLoopGroup = NioEventLoopGroup()
    private val bootstrap = Bootstrap()

    init {
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel::class.java)
                //设置客户端连接的超时时间
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,10000)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(ClientChannelInitializer(SharpConstants.DEFAULT_CLIENT_CHANNEL_OPERATOR, SharpConstants.DEFAULT_SERIALIZER))
    }

    fun newChannel(serviceReference: ServiceReference<*>):Channel{

        val serviceName = serviceReference.serviceInterface.typeName
        val providers = ProvidersLoader.getProviders(serviceReference).filter { it.protocol== ProtocolName.SHARP }.toMutableList()
        val channel:Channel? = null

        //按照权重进行连接
        for(x in providers.size-1 downTo 0 step 1) {
            val registerValue = providers[x]
            try {
                val channelFuture = this.bootstrap.clone().connect(NettyUtil.resolveAddressString(registerValue.address)).sync()
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

    fun initChannel(serviceReference: ServiceReference<*>, address: String):Channel{

        return try {
            val channelFuture = this.bootstrap.clone().connect(NettyUtil.resolveAddressString(address)).sync()
                    .addListener { future ->
                        if (future.isDone && !future.isSuccess){
                            throw IllegalArgumentException()
                        }
                    }
            channelFuture.channel()
        }catch (e: Exception){
            this.newChannel(serviceReference)
        }

    }

}