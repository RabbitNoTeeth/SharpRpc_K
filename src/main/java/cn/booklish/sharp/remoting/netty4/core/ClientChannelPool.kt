package cn.booklish.sharp.remoting.netty4.core

import io.netty.channel.Channel
import java.net.InetSocketAddress
import java.util.concurrent.ConcurrentHashMap

/**
 * 客户端连接池
 */
object ClientChannelPool {

    private val channelPoolMap = ConcurrentHashMap<String, Channel>()

    fun getChannel(serverAddress: String): Channel {
        val channel = channelPoolMap[serverAddress]
        if(channel==null || !channel.isOpen){
            val newChannel = Client.newChannel(serverAddress)
            channelPoolMap.putIfAbsent(serverAddress, newChannel)
            return newChannel
        }
        return channel
    }

}