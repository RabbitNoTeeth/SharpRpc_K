package cn.booklish.sharp.client.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.handler.timeout.WriteTimeoutException;

public class HeartBeatHandler extends ChannelOutboundHandlerAdapter {

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        if(cause instanceof WriteTimeoutException){
            System.out.println("[Rpc-Server]: 超过指定时间没有向服务器发起请求,关闭该Channel连接");
        }else{
            System.out.println("[Rpc-Server]: 客户端入站流发生异常,打印异常信息如下");
            cause.printStackTrace();
            ctx.close();
        }

    }
}
