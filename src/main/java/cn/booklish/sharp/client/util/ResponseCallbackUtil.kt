package cn.booklish.sharp.client.util

import io.netty.channel.Channel
import io.netty.util.AttributeKey
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

object ChannelAttributeUtils {

    val key = AttributeKey.valueOf<ConcurrentHashMap<Int,ResponseCallbackBean>>("dataMap")

    fun putResponseCallback(channel: Channel, id: Int, callback: ResponseCallbackBean) {
        channel.attr(key).get()[id] = callback
    }

    fun getResponseCallback(channel: Channel, id: Int): ResponseCallbackBean? {
        return channel.attr(key).get().remove(id)
    }
}

/**
 * 封装Rpc响应数据的实体
 */
class ResponseCallbackBean {

    @Volatile var result:Any? = null

    val lock = java.lang.Object()

    fun receiveMessage(result: Any?) {
        synchronized(lock) {
            this.result = result
            lock.notify()
        }
    }

}

/**
 * Rpc请求id生成器
 */
object RpcRequestIdGenerator{

    private val integer = AtomicInteger()

    fun getId(): Int {
        return integer.getAndIncrement()
    }
}
