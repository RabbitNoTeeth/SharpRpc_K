package cn.booklish.sharp.server.manage;

import java.util.concurrent.Callable;

/**
 * @author: 刘新冬(www.booklish.cn)
 * @date: 2017/12/2 16:12
 * @desc: Rpc异步计算类
 */
public class RpcAsyncComputeCallable implements Callable<Object>{

    private final RpcRequestManager.RpcRequestHandler handler;

    public RpcAsyncComputeCallable(RpcRequestManager.RpcRequestHandler handler) {
        this.handler = handler;
    }

    @Override
    public Object call() throws Exception {
        return handler.computeRpcRequest();
    }

}
