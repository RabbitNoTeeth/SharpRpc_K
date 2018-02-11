package `fun`.bookish.sharp.compute

import `fun`.bookish.sharp.manage.bean.ServiceManager
import `fun`.bookish.sharp.model.RpcRequest
import `fun`.bookish.sharp.model.RpcResponse
import java.lang.IllegalStateException
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Future

/**
 * Rpc请求管理器,用于计算Rpc请求并返回结果
 */
object ComputeTaskManager {

    private val exec = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()*2)

    /**
     * 提交Rpc注册任务
     */
    fun submit(rpcRequest: RpcRequest): Future<RpcResponse> {
        return submitAsync(rpcRequest)
    }

    /**
     * 异步计算Rpc请求
     */
    private fun submitAsync(rpcRequest: RpcRequest): Future<RpcResponse>{
        return exec.submit(Callable<RpcResponse> { computeRpcRequest(rpcRequest) })
    }

    /**
     * 进行rpc请求计算
     */
    private fun computeRpcRequest(rpcRequest: RpcRequest): RpcResponse {
        return try {
            val tempBean = ServiceManager.get(rpcRequest.serviceName)?:
                    throw IllegalStateException("there is not a impl of service \"${rpcRequest.serviceName}\" in the provider")
            val method = tempBean.serviceInterface.getMethod(rpcRequest.methodName, *rpcRequest.paramTypes)
            val invoke = method.invoke(tempBean.serviceRef, *rpcRequest.paramValues)
            RpcResponse(rpcRequest.id,true).result(invoke)
        } catch (e: Exception) {
            //e.printStackTrace()
            RpcResponse(rpcRequest.id,false).error(e)
        }
    }

}
