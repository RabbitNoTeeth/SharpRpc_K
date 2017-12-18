package cn.booklish.sharp.remoting.netty4.api

import cn.booklish.sharp.model.RpcRequest
import cn.booklish.sharp.serialize.GsonSerializer
import cn.booklish.sharp.serialize.RpcMessageSerializer
import cn.booklish.sharp.compute.RpcRequestManager
import io.netty.buffer.ByteBuf
import io.netty.channel.Channel
import io.netty.handler.timeout.ReadTimeoutException
import io.netty.util.ReferenceCountUtil
import org.apache.log4j.Logger


class ServerChannelInOperator :ChannelOperator {

    private val logger: Logger = Logger.getLogger(this.javaClass)

    override fun connected(channel: Channel) {
        logger.info("[SharpRpc-server]: 客户端连接" + channel.id() + "准备就绪")
    }

    override fun disconnected(channel: Channel) {
        logger.info("[SharpRpc-server]: 客户端连接" + channel.id() + "已关闭")
    }

    override fun send(channel: Channel, message: Any) {
        logger.info("[SharpRpc-server]: 来自客户端连接" + channel.id() + "的Rpc请求处理完成,返回结果给客户端")
        channel.writeAndFlush(message)
    }

    override fun receive(channel: Channel, message: Any) {
        logger.info("[SharpRpc-server]: 接收到来自客户端连接" + channel.id() + "的Rpc请求,开始处理")
        try{
            val rpcRequest = GsonSerializer.jsonToObject(RpcMessageSerializer.bytesToObject(message as ByteBuf,String::class.java),
                    RpcRequest::class.java)
            val computeResult = RpcRequestManager.submit(rpcRequest)
            channel.write(RpcMessageSerializer.objectToBytes(GsonSerializer.objectToJson(computeResult)))
        }finally {
            ReferenceCountUtil.release(message)
        }
    }

    override fun caught(channel: Channel, exception: Throwable) {
        if (exception is ReadTimeoutException) {
            logger.error("[SharpRpc-server]: 服务器超过指定时间没有接收到来自客户端的信息,关闭客户端Channel连接")
        } else {
            logger.error("[SharpRpc-server]: 服务器Rpc消息处理器捕获到异常,请查看详细的异常堆栈打印")
            exception.printStackTrace()
        }
        channel.close()
    }
}