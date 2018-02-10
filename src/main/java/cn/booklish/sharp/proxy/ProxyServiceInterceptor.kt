package cn.booklish.sharp.proxy

import cn.booklish.sharp.config.ServiceReference
import cn.booklish.sharp.model.RpcRequest
import cn.booklish.sharp.remoting.netty4.core.NettyClient
import net.sf.cglib.proxy.MethodInterceptor
import net.sf.cglib.proxy.MethodProxy
import java.lang.reflect.Method

/**
 * 客户端Rpc服务代理类的方法拦截器,实现了cglib的MethodInterceptor
 */
class ProxyServiceInterceptor(private val serviceReference: ServiceReference<*>, firstAddress:String): MethodInterceptor {

    private var channel = NettyClient.initChannel(serviceReference,firstAddress)

    override fun intercept(obj: Any, method: Method, args: Array<Any>, methodProxy: MethodProxy): Any? {

        if(!channel.isOpen || !channel.isActive){
            channel = NettyClient.newChannel(serviceReference)
        }
        val id = RpcRequestIdGenerator.getId()
        RpcResponseManager.add(id)
        val rpcRequest = RpcRequest(id, serviceReference.serviceInterface.typeName, method.name,method.parameterTypes,args)
        channel.writeAndFlush(rpcRequest).sync()
        return RpcResponseManager.get(id)
    }

}