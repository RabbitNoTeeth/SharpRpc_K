package cn.booklish.sharp.proxy

import cn.booklish.sharp.config.ServiceReference
import cn.booklish.sharp.model.RpcRequest
import cn.booklish.sharp.protocol.api.ProtocolName
import cn.booklish.sharp.remoting.netty4.core.Client
import io.netty.channel.Channel
import net.sf.cglib.proxy.MethodInterceptor
import net.sf.cglib.proxy.MethodProxy
import java.lang.IllegalStateException
import java.lang.reflect.Method
import java.util.*

/**
 * 客户端Rpc服务代理类的方法拦截器,实现了cglib的MethodInterceptor
 */
class ProxyServiceInterceptor(private val serviceReference: ServiceReference<*>): MethodInterceptor {

    private var channel = Client.newChannel(serviceReference)

    override fun intercept(obj: Any, method: Method, args: Array<Any>, methodProxy: MethodProxy): Any? {

        if(!channel.isOpen || !channel.isActive){
            channel = Client.newChannel(serviceReference)
        }
        val id = RpcRequestIdGenerator.getId()
        RpcResponseManager.add(id)
        val rpcRequest = RpcRequest(id, serviceReference.serviceInterface.typeName, method.name,method.parameterTypes,args)
        channel.writeAndFlush(rpcRequest).sync()
        return RpcResponseManager.get(id)
    }

}