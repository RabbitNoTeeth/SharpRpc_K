package cn.booklish.sharp.server.manage;


import cn.booklish.sharp.model.RpcRequest;
import cn.booklish.sharp.model.RpcResponse;

import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author: 刘新冬(www.booklish.cn)
 * @date: 2017/12/2 16:13
 * @desc: Rpc请求消息管理器
 */
public class ServerRpcRequestManager {

    private static AtomicReference<ServerRpcRequestManager> instance;

    //Rpc请求消息异步处理线程池
    private final ExecutorService exec;

    private final ServiceBeanFactory serviceBeanFactory;

    private ServerRpcRequestManager(int threadPoolSize,ServiceBeanFactory serviceBeanFactory) {
        this.exec = Executors.newFixedThreadPool(threadPoolSize);
        this.serviceBeanFactory = serviceBeanFactory;
    }

    public static ServerRpcRequestManager getInstance(int threadPoolSize,ServiceBeanFactory serviceBeanFactory){
        instance.compareAndSet(null,new ServerRpcRequestManager(threadPoolSize,serviceBeanFactory));
        return instance.get();
    }

    /**
     * 同步处理Rpc请求消息
     * @param rpcRequest
     * @return
     * @throws Exception
     */
    public Object submit(RpcRequest rpcRequest) throws Exception {
        return new RpcRequestHandler(rpcRequest, serviceBeanFactory).computeRpcRequest();
    }

    /**
     * 异步处理Rpc请求消息
     * @param rpcRequest
     * @return
     * @throws Exception
     */
    public Object submitAsync(RpcRequest rpcRequest) throws Exception {
        Future<Object> future = exec.submit(new ServerRpcAsyncComputeCallable(new RpcRequestHandler(rpcRequest, serviceBeanFactory)));
        return future.get();
    }

    /**
     * Rpc请求消息处理类
     */
    static class RpcRequestHandler{

        private final RpcRequest rpcRequest;

        private final ServiceBeanFactory serviceBeanFactory;

        public RpcRequestHandler(RpcRequest rpcRequest, ServiceBeanFactory serviceBeanFactory){
            this.rpcRequest = rpcRequest;
            this.serviceBeanFactory = serviceBeanFactory;
        }

        /**
         * 计算Rpc请求并返回结果
         * @return
         * @throws Exception
         */
        public Object computeRpcRequest() {
            try{
                Class<?> serviceClass= Class.forName(rpcRequest.getServiceName());
                Method method = serviceClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
                Object invoke = method.invoke(serviceBeanFactory.getServiceBean(serviceClass), rpcRequest.getParamValues());
                return new RpcResponse(rpcRequest.getId(),invoke,true);
            }catch (Exception e){
                e.printStackTrace();
                return new RpcResponse(rpcRequest.getId(),false,e);
            }
        }
    }

}
