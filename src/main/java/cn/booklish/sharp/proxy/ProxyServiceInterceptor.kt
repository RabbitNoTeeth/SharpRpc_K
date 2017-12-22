package cn.booklish.sharp.proxy

import cn.booklish.sharp.remoting.netty4.core.ClientChannelPool
import cn.booklish.sharp.model.RpcRequest
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
        val channelPool = ClientChannelPool.getChannelPool(location)

        val id = RpcRequestIdGenerator.getId()
        RpcResponseManager.add(id)
        val rpcRequest = RpcRequest(id, serviceName, method.name,method.parameterTypes,args)

        val channel = channelPool.getChannel()
        try{
            channel.writeAndFlush(rpcRequest).sync()
        }finally {
            channelPool.releaseChannel(channel)
        }

        val result = RpcResponseManager.get(id)?.take()
        RpcResponseManager.remove(id)
        return result
    }

}