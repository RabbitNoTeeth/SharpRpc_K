package cn.booklish.sharp.proxy

import cn.booklish.sharp.remoting.netty4.core.ClientChannelPool
import cn.booklish.sharp.model.RpcRequest
import net.sf.cglib.proxy.MethodInterceptor
import net.sf.cglib.proxy.MethodProxy
import java.lang.reflect.Method

/**
 * 客户端Rpc服务代理类的方法拦截器,实现了cglib的MethodInterceptor
 */
class ProxyServiceInterceptor(private val serviceName:String,private val serverAddress: String): MethodInterceptor {

    override fun intercept(obj: Any, method: Method, args: Array<Any>, methodProxy: MethodProxy): Any? {

        val channel = ClientChannelPool.getChannel(serverAddress,serviceName)
        val id = RpcRequestIdGenerator.getId()
        RpcResponseManager.add(id)
        val rpcRequest = RpcRequest(id, serviceName, method.name,method.parameterTypes,args)
        channel.writeAndFlush(rpcRequest).sync()
        return RpcResponseManager.get(id)
    }

}