package cn.booklish.sharp.proxy

import cn.booklish.sharp.config.ServiceReference
import cn.booklish.sharp.protocol.api.ProtocolName
import net.sf.cglib.proxy.Enhancer
import org.apache.log4j.Logger
import java.lang.IllegalStateException
import java.rmi.Naming
import java.rmi.Remote
import java.util.*

/**
 * Rpc客户端,用于获取Rpc服务代理类
 */
object ServiceProxyFactory {

    private val logger:Logger = Logger.getLogger(this.javaClass)

    private val random = Random()

    /**
     * 获得service服务代理
     */
    fun <T> getService(serviceReference: ServiceReference<T>): T {

        val providers = ProvidersLoader.getProviders(serviceReference)

        //随机获取一个服务提供者
        var x = random.nextInt(providers.size)
        var registerValue = providers[x]

        while (true){

            try{
                when(registerValue.protocol){
                    ProtocolName.RMI -> {
                        if(serviceReference.serviceInterface !is Remote)
                            throw IllegalStateException("服务[${serviceReference.serviceInterface.typeName}]未实现java.lang.Remote接口,无法通过rmi获取")
                        return Naming.lookup(registerValue.address) as T
                    }
                    ProtocolName.SHARP -> {
                        val proxy = ProxyServiceInterceptor(serviceReference)
                        val enhancer = Enhancer()
                        enhancer.setSuperclass(serviceReference.serviceInterface)
                        // 回调方法
                        enhancer.setCallback(proxy)
                        // 创建代理对象
                        return enhancer.create() as T
                    }
                }
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