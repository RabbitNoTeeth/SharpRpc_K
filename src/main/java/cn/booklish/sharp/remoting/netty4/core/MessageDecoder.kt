package cn.booklish.sharp.remoting.netty4.core

import cn.booklish.sharp.serialize.api.RpcSerializer
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder

/**
 * @Author: liuxindong
 * @Description:  Rpc消息解码器
 * @Created: 2017/12/19 13:39
 * @Modified:
 */
class MessageDecoder(private val rpcSerializer: RpcSerializer): ByteToMessageDecoder() {

    private val headLength = 4

    override fun decode(ctx: ChannelHandlerContext, byteBuf: ByteBuf, list: MutableList<Any>) {

        val messageSize = byteBuf.getUnsignedInt(0).toInt()

        if(byteBuf.readableBytes() < messageSize + headLength){
            return
        }

        byteBuf.skipBytes(headLength)

        val slice = byteBuf.retainedSlice(byteBuf.readerIndex(), messageSize)

        byteBuf.readerIndex(messageSize + headLength)

        val byteArray = ByteArray(slice.readableBytes())

        slice.getBytes(slice.readerIndex(),byteArray)

        list.add(rpcSerializer.deserialize(byteArray))

    }
}