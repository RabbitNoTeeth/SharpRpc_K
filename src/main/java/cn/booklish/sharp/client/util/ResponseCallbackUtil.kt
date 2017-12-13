package cn.booklish.sharp.client.util

import io.netty.channel.Channel
import io.netty.util.AttributeKey
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

/**
 * @Author: liuxindong
 * @Description:  channel属性设置工具类
 * @Created: 2017/12/13 8:49
 * @Modified:
 */
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
 * @Author: liuxindong
 * @Description:  封装Rpc响应数据的实体
 * @Created: 2017/12/13 8:49
 * @Modified:
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
 * @Author: liuxindong
 * @Description:  Rpc请求id生成器
 * @Created: 2017/12/13 8:49
 * @Modified:
 */
object RpcRequestIdGenerator{

    private val integer = AtomicInteger()

    fun getId(): Int {
        return integer.getAndIncrement()
    }
}
