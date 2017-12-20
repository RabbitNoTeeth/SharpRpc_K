package cn.booklish.sharp.serialize.kryo

import cn.booklish.sharp.serialize.api.RpcSerializer
import com.esotericsoftware.kryo.pool.KryoPool
import com.esotericsoftware.kryo.io.Output
import com.esotericsoftware.kryo.io.Input
import java.io.ByteArrayOutputStream


/**
 * @Author: liuxindong
 * @Description:  Kryo序列化方式实现类
 * @Created: 2017/12/20 9:46
 * @Modified:
 */
class KryoSerializer:RpcSerializer {

    private val pool: KryoPool =  KryoPoolFactory.getPool()

    override fun serialize(obj: Any): ByteArray {
        val kryo = pool.borrow()
        val out = Output(ByteArrayOutputStream())
        kryo.writeClassAndObject(out, obj)
        out.close()
        pool.release(kryo)
        return out.buffer
    }

    override fun deserialize(byteArray: ByteArray): Any {
        val kryo = pool.borrow()
        val input = Input(byteArray)
        val result = kryo.readClassAndObject(input)
        input.close()
        pool.release(kryo)
        return result
    }

}