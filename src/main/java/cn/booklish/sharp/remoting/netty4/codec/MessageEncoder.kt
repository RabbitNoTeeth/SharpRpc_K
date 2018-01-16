package cn.booklish.sharp.remoting.netty4.codec

import cn.booklish.sharp.serialize.GsonUtil
import cn.booklish.sharp.serialize.api.RpcSerializer
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder

/**
 * Rpc消息编码器
 */
class MessageEncoder(private val rpcSerializer: RpcSerializer): MessageToByteEncoder<Any>() {

    override fun encode(ctx: ChannelHandlerContext, obj: Any, out: ByteBuf) {
        val bytes = rpcSerializer.serialize(GsonUtil.objectToJson(obj))
        out.writeInt(bytes.size)
        out.writeBytes(bytes)
    }

}