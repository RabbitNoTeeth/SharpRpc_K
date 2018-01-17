package cn.booklish.sharp.remoting.netty4.core

import io.netty.channel.Channel
import java.util.concurrent.ConcurrentHashMap

/**
 * 客户端连接池
 */
object ClientChannelPool {

    private val channelPoolMap = ConcurrentHashMap<String, Channel>()

    fun getChannel(serverAddress: String,serviceName: String): Channel {

        val key = serverAddress + "/" + serviceName

        val channel = channelPoolMap[key]

        return if(channel==null || !channel.isOpen){

            //如果channel为空或者不可用,那么创建新channel
            val newChannel = Client.newChannel(serverAddress)
            if(channelPoolMap.putIfAbsent(key,newChannel) != null){

                //新channel添加失败,说明已经有其他线程优先添加了该服务对应的channel,
                //那么关闭新建的channel,返回map中已存在的
                newChannel.close()
                channelPoolMap[key]!!
            }else{

                //新channel添加成功,直接返回新channel
                newChannel
            }
        }else{
            channel
        }

    }

}