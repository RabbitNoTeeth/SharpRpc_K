package cn.booklish.sharp.compute

import cn.booklish.sharp.model.RpcRequest
import cn.booklish.sharp.model.RpcResponse
import cn.booklish.sharp.remoting.netty4.core.ServerConfig
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * @Author: liuxindong
 * @Description:  Rpc请求管理器,用于计算Rpc请求并返回结果
 * @Created: 2017/12/13 8:58
 * @Modified:
 */
object RpcRequestComputeManager {

    private lateinit var exec: ExecutorService

    private lateinit var serviceBeanFactory: ServiceBeanFactory

    private lateinit var serverConfig: ServerConfig

    /**
     * 启动管理器
     */
    fun start(serverConfig: ServerConfig,serviceBeanFactory: ServiceBeanFactory){
        this.serviceBeanFactory = serviceBeanFactory
        this.serverConfig = serverConfig
        this.exec = Executors.newFixedThreadPool(serverConfig.computeThreadPoolSize)
    }

    /**
     * 提交Rpc注册任务
     */
    fun submit(rpcRequest: RpcRequest): RpcResponse {
        if(serverConfig.asyncComputeRpcRequest){
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
        return exec.submit(Callable<RpcResponse> { RpcRequestHandler.computeRpcRequest(rpcRequest, serviceBeanFactory) }).get()
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

