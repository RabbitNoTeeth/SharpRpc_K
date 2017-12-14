package cn.booklish.sharp.client.proxy

import cn.booklish.sharp.client.pool.ClientChannelManager
import cn.booklish.sharp.client.util.ChannelAttributeUtils
import cn.booklish.sharp.client.util.ResponseCallbackBean
import cn.booklish.sharp.client.util.RpcRequestIdGenerator
import cn.booklish.sharp.model.RpcRequest
import cn.booklish.sharp.util.GsonUtil
import cn.booklish.sharp.util.RpcMessageSerializerUtil
import net.sf.cglib.proxy.MethodInterceptor
import net.sf.cglib.proxy.MethodProxy
import java.lang.reflect.Method
import java.net.InetSocketAddress

/**
 * @Author: liuxindong
 * @Description:  客户端Rpc服务代理类的方法拦截器,实现了cglib的MethodInterceptor
 * @Created: 2017/12/13 8:47
 * @Modified:
 */
class ProxyServiceInterceptor(private val location: InetSocketAddress, private val serviceName:String): MethodInterceptor {

    override fun intercept(obj: Any, method: Method, args: Array<Any>, methodProxy: MethodProxy): Any? {
        val channel = ClientChannelManager.getChannel(location)
        channel?.let { channel ->
            val id = RpcRequestIdGenerator.getId()
            val callback = ResponseCallbackBean()
            ChannelAttributeUtils.putResponseCallback(channel, id, callback)
            synchronized(callback.lock) {
                val rpcRequest = RpcRequest(id, serviceName, method.name)
                rpcRequest.paramTypes = method.parameterTypes
                rpcRequest.paramValues = args
                channel.writeAndFlush(RpcMessageSerializerUtil.objectToBytes(GsonUtil.objectToJson(rpcRequest))).sync()
                callback.lock.wait()
            }
            return callback.result?.let { result -> GsonUtil.objectToJson(result) }
        }
        return null
    }

}