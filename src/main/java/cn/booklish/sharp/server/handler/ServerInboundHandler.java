package cn.booklish.sharp.server.handler;

import cn.booklish.sharp.server.manage.RpcRequestManager;
import cn.booklish.sharp.model.RpcRequest;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.util.ReferenceCountUtil;
import org.apache.log4j.Logger;

/**
 * @author: 刘新冬(www.booklish.cn)
 * @date: 2017/12/2 15:57
 * @desc: Rpc请求处理器
 */
@ChannelHandler.Sharable
public class ServerInboundHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = Logger.getLogger(ServerInboundHandler.class);

    private final boolean asyncComputeRpcRequest;

    /**
     * 使用默认的Rpc请求消息处理方式:在当前线程中计算请求结果
     */
    public ServerInboundHandler(){
        asyncComputeRpcRequest = false;
    }

    /**
     * 自定义Rpc请求消息的处理方式:是否异步处理
     */
    public ServerInboundHandler(boolean asyncComputeRpcRequest){
        this.asyncComputeRpcRequest = asyncComputeRpcRequest;
    }

    /**
     * 处理连接注册事件
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        logger.info("[SharpRpc]: 客户端连接"+ctx.channel().id()+"注册成功");
        super.channelRegistered(ctx);
    }

    /**
     * 处理连接注销事件
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        logger.info("[SharpRpc]: 客户端连接"+ctx.channel().id()+"注销成功");
        super.channelUnregistered(ctx);
    }

    /**
     * 处理Rpc请求信息
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        logger.info("[SharpRpc]: 接收到来自客户端连接"+ctx.channel().id()+"的Rpc请求,开始处理");

        try{
            RpcRequest rpcRequest = (RpcRequest) msg;
            Object computeResult;
            if(rpcRequest.isAsync()){                   // 1.首先根据客户端请求判断是否需要异步处理
                //异步计算请求结果
                computeResult = RpcRequestManager.submitAsync(rpcRequest);
            }else{
                if(asyncComputeRpcRequest){             // 2.客户端不需要异步处理,那么判断服务器自身设置是否使用异步处理
                    //异步计算请求结果
                    computeResult = RpcRequestManager.submitAsync(rpcRequest);
                }else {
                    //同步计算请求结果
                    computeResult = RpcRequestManager.submit(rpcRequest);
                }
            }
            ctx.write(computeResult);
        }finally {
            ReferenceCountUtil.release(msg);
        }

    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        logger.info("[SharpRpc]: 来自客户端连接"+ctx.channel().id()+"的Rpc请求处理完成,返回结果给客户端");
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if(cause instanceof ReadTimeoutException){
            logger.error("[SharpRpc]: 服务器超过指定时间没有接收到来自客户端的信息,关闭客户端对应的Channel连接");
        }else{
            System.out.println("[Rpc-Server]: 服务器入站流发生异常,打印异常信息如下");
            logger.error("[SharpRpc]: 服务器Rpc消息处理器捕获到异常,请查看详细的异常堆栈打印");
            cause.printStackTrace();
        }
        ctx.close();
    }
}
