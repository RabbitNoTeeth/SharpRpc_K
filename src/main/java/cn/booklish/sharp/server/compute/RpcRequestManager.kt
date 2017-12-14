package cn.booklish.sharp.server.compute

import cn.booklish.sharp.model.RpcRequest
import cn.booklish.sharp.model.RpcResponse
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * @Author: liuxindong
 * @Description:  Rpc请求管理器,用于计算Rpc请求并返回结果
 * @Created: 2017/12/13 8:58
 * @Modified:
 */
object RpcRequestManager{

    private lateinit var exec: ExecutorService

    private lateinit var serviceBeanFactory:ServiceBeanFactory

    private var async = true

    private var threadPoolSize = 2

    /**
     * 启动管理器
     */
    fun start(serviceBeanFactory:ServiceBeanFactory,async:Boolean?,threadPoolSize:Int?){
        async?.let { this.async = it }
        threadPoolSize?.let { this.threadPoolSize = it }
        this.exec = Executors.newFixedThreadPool(this.threadPoolSize)
        this.serviceBeanFactory = serviceBeanFactory
    }

    /**
     * 提交Rpc注册任务
     */
    fun submit(rpcRequest: RpcRequest): RpcResponse {
        if(rpcRequest.async){
            return submitAsync(rpcRequest)
        }
        if(async){
            return submitAsync(rpcRequest)
        }
        return submitSync(rpcRequest)
    }

    /**
     * 同步计算Rpc请求
     */
    private fun submitSync(rpcRequest: RpcRequest): RpcResponse {
        return RpcRequestHandler.computeRpcRequest(rpcRequest, serviceBeanFactory)
    }

    /**
     * 异步计算Rpc请求
     */
    private fun submitAsync(rpcRequest: RpcRequest): RpcResponse{
        val call = exec.submit(RpcAsyncComputeCallable(rpcRequest, serviceBeanFactory))
        return call.get()
    }

}

/**
 * @Author: liuxindong
 * @Description:  进行异步计算Rpc请求的callable
 * @Created: 2017/12/13 8:59
 * @Modified:
 */
class RpcAsyncComputeCallable(private val rpcRequest: RpcRequest, private val serviceBeanFactory: ServiceBeanFactory): Callable<RpcResponse>{
    override fun call(): RpcResponse {
        return RpcRequestHandler.computeRpcRequest(rpcRequest,serviceBeanFactory)
    }
}

/**
 * @Author: liuxindong
 * @Description:  实际进行计算Rpc请求的处理器
 * @Created: 2017/12/13 8:59
 * @Modified:
 */
object RpcRequestHandler{
    fun computeRpcRequest(rpcRequest: RpcRequest,serviceBeanFactory: ServiceBeanFactory): RpcResponse {
        return try {
            val serviceClass = Class.forName(rpcRequest.serviceName)
            val method = serviceClass.getMethod(rpcRequest.methodName, *rpcRequest.paramTypes)
            val invoke = method.invoke(serviceBeanFactory.getServiceBean(serviceClass), *rpcRequest.paramValues)
            RpcResponse(rpcRequest.id, invoke)
        } catch (e: Exception) {
            e.printStackTrace()
            RpcResponse(rpcRequest.id, null, false, e)
        }
    }
}

