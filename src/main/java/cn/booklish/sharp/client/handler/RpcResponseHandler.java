package cn.booklish.sharp.client.handler;

import cn.booklish.sharp.client.util.ChannelAttributeUtils;
import cn.booklish.sharp.client.util.ResponseCallback;
import cn.booklish.sharp.model.RpcResponse;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @Author: liuxindong
 * @Description:  Rpc请求响应处理器
 * @Create: 2017/11/23 10:16
 * @Modify:
 */
@ChannelHandler.Sharable
public class RpcResponseHandler extends SimpleChannelInboundHandler<Object> {


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("[Rpc-Client]: channel-"+ctx.channel().id()+" 断开连接");
    }

    protected void channelRead0(ChannelHandlerContext ctx, Object response) throws Exception {

        RpcResponse rpcResponse = (RpcResponse) response;
        Channel channel = ctx.channel();
        ResponseCallback callback = ChannelAttributeUtils.getResponseCallback(channel, rpcResponse.getId());
        if(rpcResponse.isSuccess()){
            callback.receiveMessage(rpcResponse.getResult());
        }else{
            System.out.println("[Rpc异常 : 请求id="+rpcResponse.getId()+"] : 服务器计算出现异常,请检查客户端调用参数或者服务器日志");
            callback.receiveMessage(rpcResponse.getE());
        }


    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        System.out.println("[Rpc-Client]: 客户端入站流发生异常,打印异常信息如下");
        cause.printStackTrace();
        ctx.close();

    }

}
