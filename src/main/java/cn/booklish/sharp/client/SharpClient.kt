package cn.booklish.sharp.client

import cn.booklish.sharp.client.proxy.ProxyServiceInterceptor
import cn.booklish.sharp.zookeeper.ZkClient
import net.sf.cglib.proxy.Enhancer
import org.apache.commons.lang3.StringUtils
import org.apache.log4j.Logger
import java.net.InetSocketAddress

/**
 * Rpc客户端
 */
object SharpClient {

    val logger:Logger = Logger.getLogger(this.javaClass)

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
        val data = ZkClient.getData(path, String::class.java)
        // data格式为->   服务全类名;ip地址:端口
        if (StringUtils.isNotBlank(data)) {
            val split_1 = data.split(";".toRegex())
            val split_2 = split_1[1].split(":".toRegex())
            return ProxyServiceInterceptor(
                    InetSocketAddress(split_2[0], split_2[1].toInt()), split_1[0])
        }
        logger.warn("[SharpRpc-client]: 未找到名称为[$path]的Rpc服务")
        return null
    }

}