package cn.booklish.sharp.remoting.netty4.api

import cn.booklish.sharp.model.RpcResponse
import cn.booklish.sharp.proxy.RpcResponseManager
import cn.booklish.sharp.serialize.GsonUtil
import io.netty.channel.Channel
import org.apache.log4j.Logger

/**
 * 客户端channel处理类
 */
class ClientChannelOperator :ChannelOperator {

    private val logger: Logger = Logger.getLogger(this.javaClass)

    override fun connected(channel: Channel) {
    }

    override fun disconnected(channel: Channel) {
    }

    override fun send(channel: Channel, message: Any) {
    }

    override fun receive(channel: Channel, message: Any) {
        val response = GsonUtil.jsonToObject(message.toString() , RpcResponse::class.java)
        if(response.success){
            RpcResponseManager.update(response.id,response.result)
        }else{
            logger.warn("[SharpRpc-client]: 请求id=" + response.id + "在服务器计算时出现异常,请检查客户端调用参数或者服务器日志")
            RpcResponseManager.update(response.id,response.e)
        }
    }

    override fun caught(channel: Channel, exception: Throwable) {
        logger.error("[SharpRpc-client]: 客户端入站处理器捕获到异常,请查看详细的打印堆栈信息")
        exception.printStackTrace()
        channel.close()
    }

}