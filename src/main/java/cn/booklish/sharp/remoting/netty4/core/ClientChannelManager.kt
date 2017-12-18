package cn.booklish.sharp.remoting.netty4.core

import cn.booklish.sharp.proxy.ChannelAttributeUtils
import cn.booklish.sharp.proxy.ResponseCallbackBean
import io.netty.channel.Channel
import java.net.InetSocketAddress
import java.util.*
import java.util.concurrent.ConcurrentHashMap


/**
 * @Author: liuxindong
 * @Description:  客户端channel连接管理器
 * @Created: 2017/12/13 8:47
 * @Modified:
 */
object ClientChannelManager{

    private val channelPoolMap = ConcurrentHashMap<InetSocketAddress, ClientChannelPool>()
    var poolSize = 10
    /**
     * 使用自定义的连接池大小和eventLoopGroup大小
     */
    fun init(poolSize:Int?){
        poolSize?.let { ClientChannelManager.poolSize = it }
    }

    /**
     * 获取channel连接
     */
    fun getChannel(serverAddress:InetSocketAddress): Channel? {
        val channelPool = channelPoolMap[serverAddress]
        if(channelPool==null){
            channelPoolMap.putIfAbsent(serverAddress, ClientChannelPool(poolSize))
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
class ClientChannelPool(private val capacity:Int = 10){

    private val channels = arrayOfNulls<Channel>(capacity)
    private val locks = Array(capacity,{ Any() })


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
        return Client.newChannel(address)?.let {
            //为刚刚创建的channel，初始化channel属性
            val attribute = it.attr(ChannelAttributeUtils.key)
            val dataMap = ConcurrentHashMap<Int, ResponseCallbackBean>()
            attribute.set(dataMap)
            it
        }
    }
}
