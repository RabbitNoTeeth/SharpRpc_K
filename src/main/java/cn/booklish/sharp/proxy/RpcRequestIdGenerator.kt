package cn.booklish.sharp.proxy

import java.util.concurrent.atomic.AtomicInteger

/**
 * @Author: liuxindong
 * @Description:  Rpc请求id生成器
 * @Created: 2017/12/20 13:21
 * @Modified:
 */
object RpcRequestIdGenerator {

    private val currentId = AtomicInteger()

    fun getId(): Int {
        currentId.compareAndSet(Int.MAX_VALUE, Int.MIN_VALUE)
        return currentId.getAndIncrement()
    }

}