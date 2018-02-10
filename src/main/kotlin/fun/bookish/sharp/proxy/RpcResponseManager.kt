package `fun`.bookish.sharp.proxy

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.LinkedBlockingQueue


/**
 * Rpc响应管理器
 */
object RpcResponseManager {

    private val map:ConcurrentHashMap<Int,LinkedBlockingQueue<Any>> = ConcurrentHashMap()

    /**
     * 保存一定数量的queue供map使用,类似于缓存
     * 当rpc请求非常频繁时,会不断的put新的消息id到map中,而每个id对应的queue只使用一次便作废,
     * 当向map中添加新的消息id时,先从队列缓存中获取队列,如果获取不到,那么创建新的,
     * 同时,每个id对应的队列在使用完毕后放回到缓存中
     */
    private val QUEUE = ConcurrentLinkedQueue<LinkedBlockingQueue<Any>>()

    private val QUEUE_CAPACITY = 10

    init {
        for(i in 1..QUEUE_CAPACITY){
            QUEUE.add(LinkedBlockingQueue(1))
        }
    }

    fun add(id:Int){
        val queue = QUEUE.poll()?:LinkedBlockingQueue(1)
        map.putIfAbsent(id,queue)
    }

    fun update(id:Int,result:Any){
        map[id]?.put(result)
    }

    fun get(id:Int): Any? {
        val queue = map[id]?:return null
        val result = queue.take()
        remove(id)
        QUEUE.add(queue)
        return result
    }

    fun remove(id:Int){
        map.remove(id)
    }

}