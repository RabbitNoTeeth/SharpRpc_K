package cn.booklish.sharp.proxy

import cn.booklish.sharp.remoting.netty4.core.ClientChannelManager
import cn.booklish.sharp.model.RpcRequest
import cn.booklish.sharp.serialize.GsonSerializer
import cn.booklish.sharp.serialize.RpcMessageSerializer
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
                channel.writeAndFlush(RpcMessageSerializer.objectToBytes(GsonSerializer.objectToJson(rpcRequest))).sync()
                callback.lock.wait()
            }
            return callback.result?.let { result -> GsonSerializer.objectToJson(result) }
        }
        return null
    }

}