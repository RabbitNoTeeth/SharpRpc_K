package cn.booklish.sharp.server.register

import cn.booklish.sharp.zookeeper.ZkClient
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue


/**
 * 服务注册任务管理器
 */
object RegisterTaskManager{

    val queue = LinkedBlockingQueue<RegisterInfo>()

    var exec: ExecutorService? = null

    fun start(threadPoolSize: Int = 2){
        exec = Executors.newFixedThreadPool(threadPoolSize)
        exec!!.execute(RegisterTaskConsumer(queue))
    }

    fun submit(registerInfo: RegisterInfo){
        exec!!.execute(RegisterTaskProducer(queue, registerInfo))
    }

    fun stop(){
        exec!!.shutdown()
    }
}

/**
 * 服务注册任务生产者
 */
class RegisterTaskProducer(val queue: LinkedBlockingQueue<RegisterInfo>,val registerInfo: RegisterInfo):Runnable{
    override fun run() {
        try {
            queue.put(registerInfo)
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
        }
    }
}

/**
 * 服务注册任务消费者
 */
class RegisterTaskConsumer(val queue: LinkedBlockingQueue<RegisterInfo>):Runnable{
    override fun run() {
        while (true) {
            try {
                val entry = queue.take()
                ZkClient.createPath(entry.path, entry.value)
            } catch (e: InterruptedException) {
                break
            }

        }
        Thread.currentThread().interrupt()
    }
}

/**
 * 服务注册信息实体
 */
data class RegisterInfo(val path:String,val serviceTypeName:String,val serviceAddress:String){
    val value = "$serviceTypeName;$serviceAddress"
}
