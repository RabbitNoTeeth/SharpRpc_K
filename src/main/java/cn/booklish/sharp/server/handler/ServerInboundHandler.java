package cn.booklish.sharp.server.handler;

import cn.booklish.sharp.server.manage.RpcRequestManager;
import cn.booklish.sharp.model.RpcRequest;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.util.ReferenceCountUtil;

/**
 * @Author: liuxindong
 * @Description: Rpc请求处理器
 * @Create: 2017/11/21 11:27
 * @Modify:
 */
@ChannelHandler.Sharable
public class ServerInboundHandler extends ChannelInboundHandlerAdapter {

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

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println(">>>   [Channel-"+ctx.channel().id()+"] : 连接注册成功 ");
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println(">>>   [Channel-"+ctx.channel().id()+"] : 连接注销成功 ");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        try{
            RpcRequest rpcRequest = (RpcRequest) msg;
            System.out.println(">>>   [Channel-"+ctx.channel().id()+"] : 接收到Rpc请求信息,开始处理请求信息... ");
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
        System.out.println(">>>   [Channel-"+ctx.channel().id()+"] : Rpc请求处理完成,返回结果给客户端... ");
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if(cause instanceof ReadTimeoutException){
            System.out.println("[Rpc-Server]: 超过指定时间没有接收到来自客户端的信息,关闭该Channel连接");
        }else{
            System.out.println("[Rpc-Server]: 服务器入站流发生异常,打印异常信息如下");
            cause.printStackTrace();
            ctx.close();
        }

    }
}
