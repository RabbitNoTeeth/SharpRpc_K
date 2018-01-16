package cn.booklish.sharp.proxy

import java.util.concurrent.atomic.AtomicInteger

/**
 * Rpc请求id生成器
 */
object RpcRequestIdGenerator {

    private val currentId = AtomicInteger()

    fun getId(): Int {
        currentId.compareAndSet(Int.MAX_VALUE, Int.MIN_VALUE)
        return currentId.getAndIncrement()
    }

}