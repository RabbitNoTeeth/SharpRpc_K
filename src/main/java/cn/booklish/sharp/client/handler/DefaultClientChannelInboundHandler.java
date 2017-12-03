package cn.booklish.sharp.client.handler;

import cn.booklish.sharp.client.util.ChannelAttributeUtils;
import cn.booklish.sharp.client.util.ResponseCallback;
import cn.booklish.sharp.model.RpcResponse;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.log4j.Logger;

/**
 * @author: 刘新冬(www.booklish.cn)
 * @date: 2017/12/2 14:50
 * @desc: 客户端Rpc响应处理器
 */
@ChannelHandler.Sharable
public class DefaultClientChannelInboundHandler extends SimpleChannelInboundHandler<RpcResponse> {

    private static final Logger logger = Logger.getLogger(DefaultClientChannelInboundHandler.class);

    /**
     * 处理接受到的Rpc请求响应
     * @param ctx
     * @param response
     * @throws Exception
     */
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse response) throws Exception {

        Channel channel = ctx.channel();
        ResponseCallback callback = ChannelAttributeUtils.getResponseCallback(channel, response.getId());
        if(response.isSuccess()){
            callback.receiveMessage(response.getResult());
        }else{
            logger.warn("[SharpRpc]: 请求id="+response.getId()+"在服务器计算时出现异常,请检查客户端调用参数或者服务器日志");
            callback.receiveMessage(response.getE());
        }


    }

    /**
     * 异常处理
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        logger.error("[SharpRpc]: 客户端入站处理器捕获到异常,请查看详细的打印堆栈信息");
        cause.printStackTrace();
        ctx.close();

    }

}
