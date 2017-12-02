package cn.booklish.sharp.client.codec;

import cn.booklish.sharp.model.RpcRequest;
import cn.booklish.sharp.util.KryoUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.apache.log4j.Logger;

/**
 * @author: 刘新冬(www.booklish.cn)
 * @date: 2017/12/2 14:49
 * @desc: 客户端编码器
 */
public class KyroClientEncoder extends MessageToByteEncoder<RpcRequest> {

    private static final Logger logger = Logger.getLogger(KyroClientEncoder.class);

    /**
     * 编码出站数据
     * @param channelHandlerContext
     * @param rpcRequest
     * @param byteBuf
     */
    protected void encode(ChannelHandlerContext channelHandlerContext, RpcRequest rpcRequest, ByteBuf byteBuf) {
        byteBuf.writeBytes(KryoUtil.writeObjectToByteArray(rpcRequest));
    }

    /**
     * 异常处理
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("[SharpRpc]: 客户端编码器捕获到异常,请查看详细的打印堆栈信息");
        cause.printStackTrace();
        ctx.close();
    }

}
