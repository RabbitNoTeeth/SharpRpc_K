package cn.booklish.sharp.remoting.netty4.core

import io.netty.channel.Channel
import java.util.concurrent.Callable
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.FutureTask

/**
 * 客户端连接池
 */
object ClientChannelPool {

    private val channelPoolMap = ConcurrentHashMap<String, FutureTask<Channel>>()

    fun getChannel(serverAddress: String,serviceName: String): Channel {

        val key = serverAddress + "/" + serviceName

        var channelTask = channelPoolMap[key]

        return if(channelTask != null){
            //future非空,直接get获取连接
            channelTask.get()
        }else{
            //否则创建future
            val newTask = FutureTask<Channel>(Callable {
                Client.newChannel(serverAddress)
            })
            //添加future到map
            channelTask = channelPoolMap.putIfAbsent(key,newTask)
            if(channelTask==null){
                //添加成功,更新channelTask引用,并run建立channel连接
                channelTask = newTask
                channelTask.run()
            }
            //添加不成功,说明其他线程已经添加完毕,那么直接获取
            channelTask.get()
        }

    }

}