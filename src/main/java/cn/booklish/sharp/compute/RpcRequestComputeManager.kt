package cn.booklish.sharp.compute

import cn.booklish.sharp.model.RpcRequest
import cn.booklish.sharp.model.RpcResponse
import cn.booklish.sharp.remoting.netty4.config.ServerConfig
import java.lang.IllegalStateException
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Rpc请求管理器,用于计算Rpc请求并返回结果
 */
object RpcRequestComputeManager {

    private lateinit var exec: ExecutorService

    /**
     * 启动管理器
     */
    fun start(serverConfig: ServerConfig){
        this.exec = Executors.newFixedThreadPool(serverConfig.computeThreadPoolSize)
    }

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
            val serviceBean = RpcServiceBeanManager.get(serviceClass)?: throw IllegalStateException("服务端未找到服务[${serviceClass.typeName}的实体,无法计算rpc请求]")
            val invoke = method.invoke(serviceBean, *rpcRequest.paramValues)
            RpcResponse(rpcRequest.id,true).result(invoke)
        } catch (e: Exception) {
            e.printStackTrace()
            RpcResponse(rpcRequest.id,false).error(e)
        }
    }

}
