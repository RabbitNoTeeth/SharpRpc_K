package cn.booklish.sharp.client.pool

import cn.booklish.sharp.client.util.ChannelAttributeUtils
import cn.booklish.sharp.client.util.ResponseCallbackBean
import cn.booklish.sharp.pipeline.DefaultClientChannelInitializer
import io.netty.bootstrap.Bootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioSocketChannel
import org.apache.log4j.Logger
import java.net.InetSocketAddress
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Semaphore


/**
 * @Author: liuxindong
 * @Description:  客户端channel连接管理器
 * @Created: 2017/12/13 8:47
 * @Modified:
 */
object ClientChannelManager{

    val channelPoolMap = ConcurrentHashMap<InetSocketAddress,ClientChannelPool>()
    var poolSize = 10
    var eventLoopGroupSize = 0

    /**
     * 使用自定义的连接池大小和eventLoopGroup大小
     */
    fun init(poolSize:Int = 10,eventLoopGroupSize:Int = 0){
        this.poolSize = poolSize
        this.eventLoopGroupSize = eventLoopGroupSize
    }

    /**
     * 获取channel连接
     */
    fun getChannel(serverAddress:InetSocketAddress): Channel? {
        val channelPool = channelPoolMap[serverAddress]
        if(channelPool==null){
            channelPoolMap.putIfAbsent(serverAddress, ClientChannelPool(this.poolSize, this.eventLoopGroupSize))
        }
        return channelPoolMap[serverAddress]!!.getChannel(serverAddress)
    }

}

/**
 * 客户端channel连接池
 */
class ClientChannelPool(val capacity:Int = 10,val eventLoopGroupSize:Int = 0){

    val logger: Logger = Logger.getLogger(this.javaClass)

    val channels = arrayOfNulls<Channel>(capacity)
    val locks = Array(capacity,{ x -> Any() })
    val eventLoopGroup = NioEventLoopGroup(eventLoopGroupSize)


    /**
     * 获取channel连接
     */
    fun getChannel(address: InetSocketAddress): Channel? {

        val index = Random().nextInt(capacity)
        val channel = channels[index]
        if (channel != null && channel.isActive) {
            return channel
        }
        synchronized(locks[index]) {
            val channel = channels[index]
            if (channel != null && channel.isActive) {
                return channel
            }
            val newChannel = addNewChannelToPool(address)
            channels[index] = channel
            return newChannel
        }

    }


    private fun addNewChannelToPool(address: InetSocketAddress): Channel? {
        val bootstrap = Bootstrap()
        //设置信号量,最多允许重试3次
        val semaphore = Semaphore(3)
        do {
            try {
                bootstrap.group(eventLoopGroup)
                        .channel(NioSocketChannel::class.java)
                        .option(ChannelOption.SO_KEEPALIVE, true)
                        .option(ChannelOption.TCP_NODELAY, true)
                        .handler(DefaultClientChannelInitializer())
                val channelFuture = bootstrap.connect(address).sync()
                val channel = channelFuture.channel()
                //为刚刚创建的channel，初始化channel属性
                val attribute = channel.attr(ChannelAttributeUtils.key)
                val dataMap = ConcurrentHashMap<Int, ResponseCallbackBean>()
                attribute.set(dataMap)
                return channel
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
                //重试
                logger.info("[SharpRpc-client]: 客户端channel连接失败,重新尝试连接...")
            } catch (e: Exception) {
                e.printStackTrace()
                //重试
                logger.info("[SharpRpc-client]: 客户端channel连接失败,重新尝试连接...")
            }

        } while (semaphore.tryAcquire())
        return null
    }
}
