package cn.booklish.rpc.server.manage;

import java.util.concurrent.Callable;

/**
 * @Author: liuxindong
 * @Description: 异步处理Rpc请求
 * @Create: 2017/11/22 9:18
 * @Modify:
 */
public class RpcAsyncHandleCallable implements Callable<Object>{

    private final RpcRequestManager.RpcRequestHandler handler;

    public RpcAsyncHandleCallable(RpcRequestManager.RpcRequestHandler handler) {
        this.handler = handler;
    }

    @Override
    public Object call() throws Exception {
        return handler.computeRpcRequest();
    }

}
