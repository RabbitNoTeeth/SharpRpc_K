package cn.booklish.sharp.server.manage;

import java.util.concurrent.Callable;

/**
 * @author: 刘新冬(www.booklish.cn)
 * @date: 2017/12/2 16:12
 * @desc: Rpc异步计算类
 */
public class ServerRpcAsyncComputeCallable implements Callable<Object>{

    private final ServerRpcRequestManager.RpcRequestHandler handler;

    public ServerRpcAsyncComputeCallable(ServerRpcRequestManager.RpcRequestHandler handler) {
        this.handler = handler;
    }

    @Override
    public Object call() throws Exception {
        return handler.computeRpcRequest();
    }

}
