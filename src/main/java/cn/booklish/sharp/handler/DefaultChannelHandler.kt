package cn.booklish.sharp.handler

import cn.booklish.sharp.client.util.ChannelAttributeUtils
import cn.booklish.sharp.model.RpcRequest
import cn.booklish.sharp.model.RpcResponse
import cn.booklish.sharp.server.compute.RpcRequestManager
import cn.booklish.sharp.util.GsonUtil
import cn.booklish.sharp.util.RpcMessageSerializerUtil
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.handler.timeout.ReadTimeoutException
import org.apache.log4j.Logger


/**
 * @Author: liuxindong
 * @Description:  默认的客户端入站处理器,处理由服务器返回的Rpc响应消息
 * @Created: 2017/12/13 8:55
 * @Modified:
 */
@ChannelHandler.Sharable
class DefaultClientChannelInboundHandler: SimpleChannelInboundHandler<ByteBuf>() {

    private val logger: Logger = Logger.getLogger(this.javaClass)

    /**
     * 处理服务器返回的Rpc请求响应
     */
    override fun channelRead0(ctx: ChannelHandlerContext, byteBuf: ByteBuf) {

        val response = GsonUtil.jsonToObject(RpcMessageSerializerUtil.bytesToObject(byteBuf, String::class.java)
                , RpcResponse::class.java)
        val channel = ctx.channel()
        val callback = ChannelAttributeUtils.getResponseCallback(channel, response.id)
        callback?.let {
            if(response.success){
                it.receiveMessage(response.result)
            }else{
                logger.warn("[SharpRpc-client]: 请求id=" + response.id + "在服务器计算时出现异常,请检查客户端调用参数或者服务器日志")
                it.receiveMessage(response.e)
            }
        }

    }

    /**
     * 异常处理
     */
    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        logger.error("[SharpRpc-client]: 客户端入站处理器捕获到异常,请查看详细的打印堆栈信息")
        cause.printStackTrace()
        ctx.close()
    }

}

/**
 * @Author: liuxindong
 * @Description:  默认的服务器入站处理器,处理来自客户端的Rpc请求消息
 * @Created: 2017/12/13 8:56
 * @Modified:
 */
@ChannelHandler.Sharable
class DefaultServerChannelInboundHandler: SimpleChannelInboundHandler<ByteBuf>(){

    private val logger: Logger = Logger.getLogger(this.javaClass)

    override fun channelActive(ctx: ChannelHandlerContext) {
        logger.info("[SharpRpc-server]: 客户端连接" + ctx.channel().id() + "准备就绪")
    }

    override fun channelRegistered(ctx: ChannelHandlerContext) {
        logger.info("[SharpRpc-server]: 客户端连接" + ctx.channel().id() + "注册成功")
    }

    override fun channelUnregistered(ctx: ChannelHandlerContext) {
        logger.info("[SharpRpc-server]: 客户端连接" + ctx.channel().id() + "注销成功")
    }


    override fun channelRead0(ctx: ChannelHandlerContext, byteBuf: ByteBuf) {
        logger.info("[SharpRpc-server]: 接收到来自客户端连接" + ctx.channel().id() + "的Rpc请求,开始处理")
        val rpcRequest = GsonUtil.jsonToObject(RpcMessageSerializerUtil.bytesToObject(byteBuf,String::class.java),
                RpcRequest::class.java)
        val computeResult = RpcRequestManager.submit(rpcRequest)
        ctx.writeAndFlush(RpcMessageSerializerUtil.objectToBytes(GsonUtil.objectToJson(computeResult)))
        logger.info("[SharpRpc-server]: 来自客户端连接" + ctx.channel().id() + "的Rpc请求处理完成,返回结果给客户端")
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        if (cause is ReadTimeoutException) {
            logger.error("[SharpRpc-server]: 服务器超过指定时间没有接收到来自客户端的信息,关闭客户端Channel连接")
        } else {
            logger.error("[SharpRpc-server]: 服务器Rpc消息处理器捕获到异常,请查看详细的异常堆栈打印")
            cause.printStackTrace()
        }
        ctx.close()
    }

}



