package `fun`.bookish.sharp.compute

import `fun`.bookish.sharp.model.RpcRequest
import `fun`.bookish.sharp.model.RpcResponse
import java.lang.IllegalStateException
import java.util.concurrent.Callable
import java.util.concurrent.Executors

/**
 * Rpc请求管理器,用于计算Rpc请求并返回结果
 */
object RequestComputeManager {

    private val exec = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()*2)

    /**
     * 提交Rpc注册任务
     */
    fun submit(rpcRequest: RpcRequest): RpcResponse {
        return submitAsync(rpcRequest)
    }

    /**
     * 同步计算Rpc请求
     */
    private fun submitSync(rpcRequest: RpcRequest): RpcResponse {
        return computeRpcRequest(rpcRequest)
    }

    /**
     * 异步计算Rpc请求
     */
    private fun submitAsync(rpcRequest: RpcRequest): RpcResponse{
        return exec.submit(Callable<RpcResponse> { computeRpcRequest(rpcRequest) }).get()
    }

    /**
     * 进行rpc请求计算
     */
    private fun computeRpcRequest(rpcRequest: RpcRequest): RpcResponse {
        return try {
            val serviceClass = Class.forName(rpcRequest.serviceName)
            val method = serviceClass.getMethod(rpcRequest.methodName, *rpcRequest.paramTypes)
            val serviceBean = ServiceImplManager.get(serviceClass)?:
                    throw IllegalStateException("there is no a impl of service \"${serviceClass.typeName}\" in the provider")
            val invoke = method.invoke(serviceBean, *rpcRequest.paramValues)
            RpcResponse(rpcRequest.id,true).result(invoke)
        } catch (e: Exception) {
            //e.printStackTrace()
            RpcResponse(rpcRequest.id,false).error(e)
        }
    }

}
