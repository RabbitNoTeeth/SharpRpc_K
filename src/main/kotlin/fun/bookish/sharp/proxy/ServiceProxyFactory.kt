package `fun`.bookish.sharp.proxy

import `fun`.bookish.sharp.config.ServiceReference
import `fun`.bookish.sharp.protocol.api.ProtocolName
import net.sf.cglib.proxy.Enhancer
import org.apache.log4j.Logger
import java.lang.IllegalStateException
import java.rmi.Naming

/**
 * Rpc客户端,用于获取Rpc服务代理类
 */
object ServiceProxyFactory {

    private val logger:Logger = Logger.getLogger(this.javaClass)

    /**
     * 获得service服务代理
     */
    fun <T> getService(serviceReference: ServiceReference<T>): T {

        val serviceName = serviceReference.serviceInterface.typeName
        val providers = ServiceProvidersLoader.getProviders(serviceReference)
        var serviceProxy: T? = null

        //按照权重进行连接
        for(x in providers.size-1 downTo 0 step 1){
            val registerValue = providers[x]
            try{
                when(registerValue.protocol){
                    ProtocolName.RMI -> {
                        serviceProxy = Naming.lookup(registerValue.address) as T
                        logger.info("successfully connect to the provider \"[${registerValue.protocol.value}] ${registerValue.address}\" of service \"$serviceName\"")
                    }
                    ProtocolName.SHARP -> {
                        val proxy = ProxyServiceInterceptor(serviceReference,registerValue.address)
                        val enhancer = Enhancer()
                        enhancer.setSuperclass(serviceReference.serviceInterface)
                        // 设置回调
                        enhancer.setCallback(proxy)
                        // 创建代理对象
                        serviceProxy = enhancer.create() as T
                        logger.info("successfully connect to the provider \"[${registerValue.protocol.value}] ${registerValue.address}\" of service \"$serviceName\"")
                    }
                }
            }catch (e: Exception){
                logger.warn("failed to connect to the provider \"[${registerValue.protocol.value}] ${registerValue.address}\" of service \"$serviceName\"")
                continue
            }
        }

        return serviceProxy?:throw IllegalStateException("there is no available provider of service \"$serviceName\"")
    }

}