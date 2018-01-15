package cn.booklish.sharp.registry.manager

import cn.booklish.sharp.registry.api.RegistryCenter
import java.lang.IllegalStateException
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import kotlin.math.min

/**
 * 注册任务管理器，统一管理服务提供者的服务注册行为
 */
object RegisterTaskManager{

    private val queue = LinkedBlockingQueue<RegisterInfo>()

    @Volatile
    private var started = false

    private val exec = Executors.newFixedThreadPool(min(Runtime.getRuntime().availableProcessors()+1,32))

    fun start(registryCenter: RegistryCenter){
        started = true
        exec.execute({
            while (true) {
                try {
                    val info = queue.take()
                    registryCenter.register(info.serviceName, info.address)
                } catch (e: InterruptedException) {
                    Thread.currentThread().interrupt()
                    throw ExceptionInInitializerError("RegisterTaskManager 服务注册管理器初始化失败")
                }
            }
        })
    }

    fun submit(registerInfo: RegisterInfo){
        if(!started) throw IllegalStateException("RegisterTaskManager 服务管理器无启动, 无法接收注册任务")
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


    class RegisterInfo(val serviceName:String,val address:String)
}