package cn.booklish.sharp.server.compute

import cn.booklish.sharp.model.RpcRequest
import cn.booklish.sharp.model.RpcResponse
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Rpc请求管理器
 */
object RpcRequestManager{

    var exec: ExecutorService? = null

    var serviceBeanFactory:ServiceBeanFactory? = null

    var async = true

    /**
     * 启动管理器
     */
    fun start(serviceBeanFactory:ServiceBeanFactory,async:Boolean = true,threadPoolSize:Int = 2){
        this.async = async
        this.exec = Executors.newFixedThreadPool(threadPoolSize)
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
    fun submitSync(rpcRequest: RpcRequest): RpcResponse {
        return RpcRequestHandler.computeRpcRequest(rpcRequest, serviceBeanFactory!!)
    }

    /**
     * 异步计算Rpc请求
     */
    fun submitAsync(rpcRequest: RpcRequest): RpcResponse{
        val call = exec!!.submit(RpcAsyncComputeCallable(rpcRequest, serviceBeanFactory!!))
        return call.get()
    }

}

/**
 * 进行异步计算Rpc请求的callable
 */
class RpcAsyncComputeCallable(val rpcRequest: RpcRequest,val serviceBeanFactory: ServiceBeanFactory): Callable<RpcResponse>{
    override fun call(): RpcResponse {
        return RpcRequestHandler.computeRpcRequest(rpcRequest,serviceBeanFactory)
    }
}

/**
 * 实际进行计算Rpc请求的处理器
 */
object RpcRequestHandler{
    fun computeRpcRequest(rpcRequest: RpcRequest,serviceBeanFactory: ServiceBeanFactory): RpcResponse {
        try {
            val serviceClass = Class.forName(rpcRequest.serviceName)
            val method = serviceClass.getMethod(rpcRequest.methodName, *rpcRequest.paramTypes)
            val invoke = method.invoke(serviceBeanFactory.getServiceBean(serviceClass), *rpcRequest.paramValues)
            return RpcResponse(rpcRequest.id, invoke)
        } catch (e: Exception) {
            e.printStackTrace()
            return RpcResponse(rpcRequest.id, null, false, e)
        }
    }
}

