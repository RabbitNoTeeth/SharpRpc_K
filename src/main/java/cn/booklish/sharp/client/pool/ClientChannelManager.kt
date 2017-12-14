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

    private val channelPoolMap = ConcurrentHashMap<InetSocketAddress,ClientChannelPool>()
    var poolSize = 10
    var eventLoopGroupSize = 0

    /**
     * 使用自定义的连接池大小和eventLoopGroup大小
     */
    fun init(poolSize:Int?,eventLoopGroupSize:Int?){
        poolSize?.let { this.poolSize = it }
        eventLoopGroupSize?.let { this.eventLoopGroupSize = it }
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
 * @Author: liuxindong
 * @Description:  客户端channel连接池
 * @Created: 2017/12/13 17:26
 * @Modified:
 */
class ClientChannelPool(private val capacity:Int = 10, private val eventLoopGroupSize:Int = 0){

    private val logger: Logger = Logger.getLogger(this.javaClass)

    private val channels = arrayOfNulls<Channel>(capacity)
    private val locks = Array(capacity,{ Any() })
    private val eventLoopGroup = NioEventLoopGroup(eventLoopGroupSize)


    /**
     * 获取channel连接
     */
    fun getChannel(address: InetSocketAddress): Channel? {

        val index = Random().nextInt(capacity)
        val channel = channels[index]
        if (channel!=null && channel.isActive) {
            return channel
        }
        synchronized(locks[index]) {
            val channel2 = channels[index]
            if (channel2!=null && channel2.isActive) {
                return channel2
            }
            val newChannel = createChannel(address)
            channels[index] = newChannel!!
            return newChannel
        }

    }


    private fun createChannel(address: InetSocketAddress): Channel? {
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
