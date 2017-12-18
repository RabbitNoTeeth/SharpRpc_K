package cn.booklish.sharp.serialize

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled

/**
 * @Author: liuxindong
 * @Description:  Rpc消息序列化工具
 * @Created: 2017/12/13 9:05
 * @Modified:
 */
class RpcMessageSerializer {

    companion object{

        fun objectToBytes(obj: Any): ByteBuf {

            val buffer = Unpooled.buffer()
            buffer.writeBytes(KryoSerializer.writeObjectToByteArray(obj))
            return buffer

        }

        fun <T> bytesToObject(buffer: ByteBuf, clazz: Class<T>): T {

            //获取可读字节数
            val length = buffer.readableBytes()
            //分配一个新的数组来保存具有该长度的字节数据
            val bytes = ByteArray(length)
            //将字节复制到该数组
            buffer.readBytes(bytes)
            return KryoSerializer.readObjectFromByteArray(bytes, clazz)

        }

    }

}