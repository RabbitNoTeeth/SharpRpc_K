package cn.booklish.sharp.server.manage;


import cn.booklish.sharp.model.RpcRequest;
import cn.booklish.sharp.model.RpcResponse;
import cn.booklish.test.TestInterface;

import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @Author: liuxindong
 * @Description: Rpc请求消息管理器
 * @Create: 2017/11/21 13:40
 * @Modify:
 */
public class RpcRequestManager {

    //Rpc请求消息异步处理线程池
    private static final ExecutorService exec = Executors.newFixedThreadPool(2);

    /**
     * 同步处理Rpc请求消息
     * @param rpcRequest
     * @return
     * @throws Exception
     */
    public static Object submit(RpcRequest rpcRequest) throws Exception {
        return new RpcRequestHandler(rpcRequest).computeRpcRequest();
    }

    /**
     * 异步处理Rpc请求消息
     * @param rpcRequest
     * @return
     * @throws Exception
     */
    public static Object submitAsync(RpcRequest rpcRequest) throws Exception {
        Future<Object> future = exec.submit(new RpcAsyncComputeCallable(new RpcRequestHandler(rpcRequest)));
        return future.get();
    }

    /**
     * Rpc请求消息处理类
     */
    static class RpcRequestHandler{

        private final RpcRequest rpcRequest;

        public RpcRequestHandler(RpcRequest rpcRequest){
            this.rpcRequest = rpcRequest;
        }

        /**
         * 计算Rpc请求并返回结果
         * @return
         * @throws Exception
         */
        public Object computeRpcRequest() {
            try{
                Class<?> serviceClass= TestInterface.class;
                Method method = serviceClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
                Object invoke = method.invoke(serviceClass.newInstance(), rpcRequest.getParamValues());
                return new RpcResponse(rpcRequest.getId(),invoke,true);
            }catch (Exception e){
                return new RpcResponse(rpcRequest.getId(),false,e);
            }
        }
    }

}
