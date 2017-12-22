package cn.booklish.sharp.registry.api

import cn.booklish.sharp.remoting.netty4.core.ServerConfig
import java.io.Serializable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue


/**
 * @Author: liuxindong
 * @Description:  Rpc服务注册任务管理器
 * @Created: 2017/12/13 9:01
 * @Modified:
 */
object RegisterTaskManager{

    private val queue = LinkedBlockingQueue<RegisterInfo>()

    private lateinit var exec: ExecutorService

    fun start(serverConfig: ServerConfig){
        exec = Executors.newFixedThreadPool(serverConfig.registerThreadPoolSize)
        exec.execute({
            while (true) {
                try {
                    val info = queue.take()
                    serverConfig.registryCenter!!.createPath(info.path, info)
                } catch (e: InterruptedException) {
                    Thread.currentThread().interrupt()
                }
            }
        })
    }

    fun submit(registerInfo: RegisterInfo){
        exec.execute({
            try {
                queue.put(registerInfo)
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
            }
        })
    }

    fun stop(){
        exec.shutdown()
    }
}

/**
 * @Author: liuxindong
 * @Description:  Rpc服务注册信息实体
 * @Created: 2017/12/13 9:02
 * @Modified:
 */
data class RegisterInfo(val path:String,val serviceTypeName:String,val serviceAddress:String): Serializable
