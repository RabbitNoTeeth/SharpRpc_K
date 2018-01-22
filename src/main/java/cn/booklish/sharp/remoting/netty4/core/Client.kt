package cn.booklish.sharp.remoting.netty4.core

import cn.booklish.sharp.config.ServiceReference
import cn.booklish.sharp.constant.SharpConstants
import cn.booklish.sharp.protocol.api.ProtocolName
import cn.booklish.sharp.proxy.ProvidersLoader
import cn.booklish.sharp.remoting.netty4.handler.ClientChannelInitializer
import cn.booklish.sharp.remoting.netty4.util.NettyUtil
import io.netty.bootstrap.Bootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioSocketChannel
import org.apache.log4j.Logger
import java.lang.IllegalStateException
import java.util.*

/**
 * 客户端引导
 */
object Client {

    private val logger: Logger = Logger.getLogger(this.javaClass)

    private val eventLoopGroup = NioEventLoopGroup()
    private val bootstrap = Bootstrap()
    private val random: Random = Random()

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

        //随机获取一个服务提供者
        var x = random.nextInt(providers.size)
        var registerValue = providers[x]

        while (true){

            try{
                val channelFuture = this.bootstrap.clone().connect(NettyUtil.resolveAddressString(registerValue.address)).sync()
                        //添加监听器,判断连接是否成功,当连接超时时,进入if代码块,抛出异常
                        .addListener { future ->
                                        if (future.isDone && !future.isSuccess){
                                            throw IllegalStateException("连接到服务 $serviceName 的提供者 ${registerValue.address} 失败,尝试连接其他服务提供者")
                                        }
                                    }
                return channelFuture.channel()
            }catch (e:Exception){
                //捕获连接异常,打印日志
                logger.warn("连接到服务 $serviceName 的提供者 ${registerValue.address} 失败,尝试连接其他服务提供者")
                //从提供者列表中删除当前服务提供者
                providers.remove(registerValue)
                if(providers.isEmpty()){
                    throw IllegalStateException("服务 $serviceName 无可用连接")
                }
                //重新尝试其他鼓舞提供者
                x = random.nextInt(providers.size)
                registerValue = providers[x]
            }

        }

    }

    fun initChannel(address: String):Channel{

        val channelFuture = this.bootstrap.clone().connect(NettyUtil.resolveAddressString(address)).sync()
                .addListener { future ->
                    if (future.isDone && !future.isSuccess){
                        throw IllegalStateException("连接到服务提供者 $address 失败,尝试连接其他服务提供者")
                    }
                }
        return channelFuture.channel()

    }

}