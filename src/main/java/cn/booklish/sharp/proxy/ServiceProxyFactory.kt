package cn.booklish.sharp.proxy

import cn.booklish.sharp.protocol.api.ProtocolName
import cn.booklish.sharp.protocol.config.ProtocolConfig
import cn.booklish.sharp.remoting.netty4.config.ClientConfig
import cn.booklish.sharp.proxy.pool.ClientChannelPool
import cn.booklish.sharp.proxy.pool.RmiPool
import cn.booklish.sharp.registry.manager.RegisterTaskManager
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

        val serviceName = serviceClass.typeName.replace(".","/",false)

        val key = protocolConfig.name.value + "://" + serviceName + "?version=" + version

        return when(protocolConfig.name){
            ProtocolName.RMI -> {
                RmiPool.get(key,"$serviceName/version-$version")
            }
            ProtocolName.SHARP -> {
                val proxy = ProxyServiceInterceptor(key)
                val enhancer = Enhancer()
                enhancer.setSuperclass(serviceClass)
                // 回调方法
                enhancer.setCallback(proxy)
                // 创建代理对象
                enhancer.create()
            }
        }

    }

}