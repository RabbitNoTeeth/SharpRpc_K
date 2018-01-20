package cn.booklish.sharp.proxy.pool

import cn.booklish.sharp.protocol.config.ProtocolConfig
import cn.booklish.sharp.remoting.netty4.config.ClientConfig
import org.apache.log4j.Logger
import java.lang.IllegalStateException
import java.rmi.Naming
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.FutureTask

/**
 * 客户端连接池
 */
object RmiPool {

    private val logger: Logger = Logger.getLogger(this.javaClass)

    private val channelPoolMap = ConcurrentHashMap<String, FutureTask<Any>>()

    private lateinit var clientConfig: ClientConfig
    private lateinit var protocolConfig: ProtocolConfig

    private val random = Random()

    fun init(clientConfig: ClientConfig, protocolConfig: ProtocolConfig){
        this.clientConfig = clientConfig
        this.protocolConfig = protocolConfig
    }

    fun get(serviceKey: String,serviceName: String): Any {

        channelPoolMap[serviceKey]?.let {
            return try {
                //如果服务key对应的future存在,那就返回其连接
                val bean = it.get()
                bean
            }catch (e :Exception){
                //如果连接失败,就尝试连接其他提供者
                connect(serviceKey,serviceName)
            }
        }

        //如果服务key对应的future不存在,那么尝试连接其他提供者
        return connect(serviceKey,serviceName)

    }

    private fun connect(serviceKey: String,serviceName: String): Any{

        //获取服务提供者列表
        val serverList = getProviders(serviceKey)
        //随机获取一个服务提供者
        var x = random.nextInt(serverList.size)
        var serverAddress = serverList[x]

        while (true){
            val newTask = tryConnect(serverAddress,serviceName)
            if (newTask != null){
                try {
                    return newTask.get()
                }catch (e: Exception){
                    logger.error("创建到服务[$serviceKey]的提供者[$serverAddress]的连接失败,尝试连接其他提供者")
                }
            }
            serverList.remove(serverAddress)
            if(serverList.isEmpty()){
                throw IllegalStateException("服务[$serviceKey]无可用连接")
            }
            x = random.nextInt(serverList.size)
            serverAddress = serverList[x]
        }

    }

    /**
     * 尝试连接到服务端,如果newTask.run()连接失败,是不会有异常抛出的,异常会在newTask.get()调用时抛出
     */
    private fun tryConnect(serverAddress: String,serviceName: String): FutureTask<Any>?{

        val address = "rmi://$serverAddress/$serviceName"

        //创建future
        val newTask = FutureTask<Any>(Callable {
            Naming.lookup(address)
        })

        //添加future到map
        val addResult = channelPoolMap.putIfAbsent(serverAddress,newTask)

        return if(addResult==null){
            newTask.run()
            newTask
        }else{
            null
        }
    }

    /**
     * 获取服务提供者列表
     */
    private fun getProviders(serviceKey: String): ArrayList<String>{
        clientConfig.registryCenter!!.getProviders(serviceKey).let {
            if(it.isEmpty()){
                throw IllegalArgumentException("未找到服务[$serviceKey]提供者,无法创建服务代理")
            }

            val serverList = arrayListOf<String>()
            it.forEach { element ->
                serverList.add(element)
            }

            return serverList
        }
    }

}