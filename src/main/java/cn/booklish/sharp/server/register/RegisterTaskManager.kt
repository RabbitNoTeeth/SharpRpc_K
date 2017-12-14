package cn.booklish.sharp.server.register

import cn.booklish.sharp.zookeeper.ZkClient
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

    private var threadPoolSize = 2

    fun start(threadPoolSize: Int?){
        threadPoolSize?.let { this.threadPoolSize = it }
        exec = Executors.newFixedThreadPool(this.threadPoolSize)
        exec.execute(RegisterTaskConsumer(queue))
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
class RegisterTaskConsumer(private val queue: LinkedBlockingQueue<RegisterInfo>):Runnable{
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
 * @Author: liuxindong
 * @Description:  Rpc服务注册信息实体
 * @Created: 2017/12/13 9:02
 * @Modified:
 */
data class RegisterInfo(val path:String,val serviceTypeName:String,val serviceAddress:String){
    val value = "$serviceTypeName;$serviceAddress"
}
