package cn.booklish.sharp.proxy

import cn.booklish.sharp.config.ServiceReference
import cn.booklish.sharp.protocol.api.ProtocolName
import cn.booklish.sharp.remoting.netty4.core.Client
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

        val serviceName = serviceReference.serviceInterface.typeName

        val providers = ProvidersLoader.getProviders(serviceReference)

        //随机获取一个服务提供者
        var x = random.nextInt(providers.size)
        var registerValue = providers[x]

        while (true){

            try{
                return when(registerValue.protocol){
                    ProtocolName.RMI -> {
                        Naming.lookup(registerValue.address) as T
                    }
                    ProtocolName.SHARP -> {
                        val proxy = ProxyServiceInterceptor(serviceReference)
                        val enhancer = Enhancer()
                        enhancer.setSuperclass(serviceReference.serviceInterface)
                        // 回调方法
                        enhancer.setCallback(proxy)
                        // 创建代理对象
                        enhancer.create() as T
                    }
                }
            }catch (e:Exception){
                logger.warn("连接到服务 $serviceName 的提供者 ${registerValue.address} 失败,尝试连接其他服务提供者")
                providers.remove(registerValue)
                if(providers.isEmpty()){
                    throw IllegalStateException("服务 $serviceName 无可用连接,服务获取失败")
                }
                x = random.nextInt(providers.size)
                registerValue = providers[x]
            }

        }

    }

}