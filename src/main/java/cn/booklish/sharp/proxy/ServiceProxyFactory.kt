package cn.booklish.sharp.proxy

import cn.booklish.sharp.protocol.api.ProtocolName
import cn.booklish.sharp.protocol.config.ProtocolConfig
import cn.booklish.sharp.registry.manager.RegisterTaskManager
import cn.booklish.sharp.remoting.netty4.config.ClientConfig
import net.sf.cglib.proxy.Enhancer
import org.apache.log4j.Logger
import java.lang.IllegalStateException
import java.rmi.Naming
import java.util.*

/**
 * Rpc客户端,用于获取Rpc服务代理类
 */
object ServiceProxyFactory {

    private val logger:Logger = Logger.getLogger(this.javaClass)

    private val random = Random()

    private lateinit var clientConfig: ClientConfig
    private lateinit var protocolConfig: ProtocolConfig

    fun init(clientConfig: ClientConfig,protocolConfig: ProtocolConfig){
        this.clientConfig = clientConfig
        this.protocolConfig = protocolConfig
    }

    /**
     * 获得service服务代理
     */
    fun getService(serviceClass: Class<*>,version: String): Any {

        val serverAddress = getServiceProvider(serviceClass.typeName,version)

        val address = "rmi://${protocolConfig.host}:${protocolConfig.port}/${serviceClass.simpleName}/version-$version"

        return when(protocolConfig.name){
            ProtocolName.RMI -> {
                Naming.lookup(address)
            }
            ProtocolName.SHARP -> {
                val proxy = ProxyServiceInterceptor(serviceClass.typeName,serverAddress)
                val enhancer = Enhancer()
                enhancer.setSuperclass(serviceClass)
                // 回调方法
                enhancer.setCallback(proxy)
                // 创建代理对象
                enhancer.create()
            }
        }

    }


    /**
     * 获取服务提供者地址
     */
    private fun getServiceProvider(serviceName: String,version: String): String{
        val registryCenter = clientConfig.registryCenter?: throw IllegalStateException("无效的注册中心")
        val key = protocolConfig.name.value + "://" + serviceName + "?version=" + version
        registryCenter.getProviders(key).let {
            if(it.isEmpty()){
                throw IllegalArgumentException("未找到服务[$serviceName]提供者,无法创建服务代理")
            }
            val x = random.nextInt(it.size)
            return it.toList()[x]
        }
    }

}