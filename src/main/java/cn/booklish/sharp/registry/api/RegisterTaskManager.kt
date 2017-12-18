package cn.booklish.sharp.registry.api

import cn.booklish.sharp.constant.Constants
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

    private var threadPoolSize = Constants.DEFAULT_REGISTER_TASK_MANAGER_THREAD_POOL_SIZE

    fun start(threadPoolSize: Int?,registerClient: RegisterClient){
        threadPoolSize?.let { RegisterTaskManager.threadPoolSize = it }
        exec = Executors.newFixedThreadPool(RegisterTaskManager.threadPoolSize)
        exec.execute(RegisterTaskConsumer(queue,registerClient))
    }

    fun submit(registerInfo: RegisterInfo){
        exec.execute(RegisterTaskProducer(queue, registerInfo))
    }

    fun stop(){
        exec.shutdown()
    }
}

/**
 * @Author: liuxindong
 * @Description:  Rpc服务注册任务生产者
 * @Created: 2017/12/13 9:02
 * @Modified:
 */
class RegisterTaskProducer(private val queue: LinkedBlockingQueue<RegisterInfo>, private val registerInfo: RegisterInfo):Runnable{
    override fun run() {
        try {
            queue.put(registerInfo)
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
        }
    }
}

/**
 * @Author: liuxindong
 * @Description:  Rpc服务注册任务消费者
 * @Created: 2017/12/13 9:02
 * @Modified:
 */
class RegisterTaskConsumer(private val queue: LinkedBlockingQueue<RegisterInfo>,private val registerClient: RegisterClient):Runnable{
    override fun run() {
        while (true) {
            try {
                val info = queue.take()
                registerClient.createPath(info.path, info)
            } catch (e: InterruptedException) {
                break
            }
        }
        Thread.currentThread().interrupt()
    }
}

/**
 * @Author: liuxindong
 * @Description:  Rpc服务注册信息实体
 * @Created: 2017/12/13 9:02
 * @Modified:
 */
data class RegisterInfo(val path:String,val serviceTypeName:String,val serviceAddress:String): Serializable
