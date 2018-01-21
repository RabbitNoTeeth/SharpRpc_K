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

    private var channel = newChannel(serviceReference)

    private val random = Random()

    override fun intercept(obj: Any, method: Method, args: Array<Any>, methodProxy: MethodProxy): Any? {

        if(!channel.isOpen || !channel.isActive){
            channel = newChannel(serviceReference)
        }
        val id = RpcRequestIdGenerator.getId()
        RpcResponseManager.add(id)
        val rpcRequest = RpcRequest(id, serviceReference.serviceInterface.typeName, method.name,method.parameterTypes,args)
        channel.writeAndFlush(rpcRequest).sync()
        return RpcResponseManager.get(id)
    }

    private fun newChannel(serviceReference: ServiceReference<*>): Channel {
        val providers = ProvidersLoader.getProviders(serviceReference).filter { it.protocol==ProtocolName.SHARP }.toMutableList()

        //随机获取一个服务提供者
        var x = random.nextInt(providers.size)
        var registerValue = providers[x]

        while (true){

            try{
                return Client.newChannel(registerValue.address)
            }catch (e:Exception){
                providers.remove(registerValue)
                if(providers.isEmpty()){
                    throw IllegalStateException("服务[${serviceReference.serviceInterface.typeName}]无可用连接")
                }
                x = random.nextInt(providers.size)
                registerValue = providers[x]
            }

        }
    }

}