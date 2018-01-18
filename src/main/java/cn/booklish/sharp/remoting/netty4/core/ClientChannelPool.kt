package cn.booklish.sharp.remoting.netty4.core

import io.netty.channel.Channel
import org.apache.log4j.Logger
import java.util.concurrent.Callable
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.FutureTask

/**
 * 客户端连接池
 */
object ClientChannelPool {

    private val logger: Logger = Logger.getLogger(this.javaClass)

    private val channelPoolMap = ConcurrentHashMap<String, FutureTask<Channel>>()

    fun getChannel(serverAddress: String,serviceName: String): Channel {

        val key = serverAddress + "/" + serviceName

        return channelPoolMap[key]!!.get()

    }

    fun connect(serverAddress: String,serviceName: String): Channel?{

        val key = serverAddress + "/" + serviceName

        var channelTask = channelPoolMap[key]

        if(channelTask == null){

            //创建future
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

        }

        return try {
            channelTask.get()
        }catch (e:Exception){
            logger.error("创建到服务[$serviceName]提供者[$serverAddress]的channel连接失败,请查看异常堆栈")
            e.printStackTrace()
            null
        }
    }

}