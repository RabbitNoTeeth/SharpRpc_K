package cn.booklish.sharp.client.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.handler.timeout.WriteTimeoutException;
import org.apache.log4j.Logger;

/**
 * @author: 刘新冬(www.booklish.cn)
 * @date: 2017/12/2 14:49
 * @desc: 客户端心跳处理器
 */
public class HeartBeatHandler extends ChannelOutboundHandlerAdapter {

    private static final Logger logger = Logger.getLogger(HeartBeatHandler.class);

    /**
     * 异常处理
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        if(cause instanceof WriteTimeoutException){
            logger.error("[SharpRpc]: 客户端超过指定时间没有向服务器发起请求,关闭Channel连接");
        }else{
            logger.error("[SharpRpc]: 客户端心跳处理器捕获到异常,请查看详细的打印堆栈信息");
            cause.printStackTrace();
        }
        ctx.close();

    }
}
