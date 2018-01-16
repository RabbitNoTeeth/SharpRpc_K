package cn.booklish.sharp.proxy

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.LinkedBlockingQueue

/**
 * @Author: liuxindong
 * @Description:  Rpc响应管理器
 * @Created: 2017/12/20 10:45
 * @Modified:
 */
object RpcResponseManager {

    private val map:ConcurrentHashMap<Int,LinkedBlockingQueue<Any>> = ConcurrentHashMap()

    fun add(id:Int){
        map[id] = LinkedBlockingQueue()
    }

    fun update(id:Int,result:Any){
        map[id]?.put(result)
    }

    fun get(id:Int): LinkedBlockingQueue<Any>? {
        return map[id]
    }

    fun remove(id:Int){
        map.remove(id)
    }

}