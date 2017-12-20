package cn.booklish.sharp.serialize.kryo

import cn.booklish.sharp.model.RpcRequest
import cn.booklish.sharp.model.RpcResponse
import com.esotericsoftware.kryo.pool.KryoPool
import org.objenesis.strategy.StdInstantiatorStrategy
import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.pool.KryoFactory


/**
 * @Author: liuxindong
 * @Description:  kryo池化工厂
 * @Created: 2017/12/20 9:46
 * @Modified:
 */
object KryoPoolFactory {

    private val pool = KryoPool.Builder(KryoFactory {
        val kryo = Kryo()
        kryo.references = false
        //把已知的结构注册到Kryo注册器里面，提高序列化/反序列化效率
        kryo.register(RpcRequest::class.java)
        kryo.register(RpcResponse::class.java)
        kryo.instantiatorStrategy = StdInstantiatorStrategy()
        kryo
    }).build()

    fun getPool(): KryoPool {
        return pool
    }
}