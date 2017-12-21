package cn.booklish.sharp.proxy

import cn.booklish.sharp.constant.SharpConstants
import cn.booklish.sharp.registry.api.RegistryCenter
import net.sf.cglib.proxy.Enhancer
import org.apache.log4j.Logger
import java.net.InetSocketAddress

/**
 * @Author: liuxindong
 * @Description:  Rpc客户端,用于获取Rpc服务代理类
 * @Created: 2017/12/13 8:50
 * @Modified:
 */
object ServiceProxyFactory {

    private val logger:Logger = Logger.getLogger(this.javaClass)

    private lateinit var registryCenter: RegistryCenter

    fun init(registryCenter: RegistryCenter){
        this.registryCenter = registryCenter
    }

    /**
     * 获得service服务代理
     */
    fun getService(path: String, serviceInterface: Class<*>): Any? {

        val proxy = getServiceLocation(path) ?: return null
        val enhancer = Enhancer()
        enhancer.setSuperclass(serviceInterface)
        // 回调方法
        enhancer.setCallback(proxy)
        // 创建代理对象
        return enhancer.create()

    }

    /**
     * 获取服务地址并创建服务代理的回调
     */
    private fun getServiceLocation(path: String): ProxyServiceInterceptor? {
        return registryCenter.getData(SharpConstants.DEFAULT_REGISTER_PATH_PREFIX + path).let {
            val address = it.serviceAddress.split(":".toRegex())
            ProxyServiceInterceptor(
                    InetSocketAddress(address[0], address[1].toInt()), it.serviceTypeName)
        }
    }

}