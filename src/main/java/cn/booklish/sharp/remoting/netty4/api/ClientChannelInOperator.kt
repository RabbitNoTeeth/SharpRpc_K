package cn.booklish.sharp.remoting.netty4.api

import cn.booklish.sharp.proxy.ChannelAttributeUtils
import cn.booklish.sharp.model.RpcResponse
import cn.booklish.sharp.serialize.GsonSerializer
import cn.booklish.sharp.serialize.RpcMessageSerializer
import io.netty.buffer.ByteBuf
import io.netty.channel.Channel
import io.netty.util.ReferenceCountUtil
import org.apache.log4j.Logger


class ClientChannelInOperator:ChannelOperator {

    private val logger: Logger = Logger.getLogger(this.javaClass)

    override fun connected(channel: Channel) {
    }

    override fun disconnected(channel: Channel) {
    }

    override fun send(channel: Channel, message: Any) {
        channel.writeAndFlush(message)
    }

    override fun receive(channel: Channel, message: Any) {
        try{
            val response = GsonSerializer.jsonToObject(RpcMessageSerializer.bytesToObject(message as ByteBuf, String::class.java)
                    , RpcResponse::class.java)
            val callback = ChannelAttributeUtils.getResponseCallback(channel, response.id)
            callback?.let {
                if(response.success){
                    it.receiveMessage(response.result)
                }else{
                    logger.warn("[SharpRpc-client]: 请求id=" + response.id + "在服务器计算时出现异常,请检查客户端调用参数或者服务器日志")
                    it.receiveMessage(response.e)
                }
            }
        }finally {
            ReferenceCountUtil.release(message)
        }
    }

    override fun caught(channel: Channel, exception: Throwable) {
        logger.error("[SharpRpc-client]: 客户端入站处理器捕获到异常,请查看详细的打印堆栈信息")
        exception.printStackTrace()
        channel.close()
    }

}