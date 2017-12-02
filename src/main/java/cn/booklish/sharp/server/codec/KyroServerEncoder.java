package cn.booklish.sharp.server.codec;


import cn.booklish.sharp.model.RpcResponse;
import cn.booklish.sharp.util.KryoUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.apache.log4j.Logger;

/**
 * @Author: liuxindong
 * @Description: Kyro编码器
 * @Create: 2017/11/21 11:16
 * @Modify:
 */
public class KyroServerEncoder extends MessageToByteEncoder<RpcResponse>{

    private static final Logger logger = Logger.getLogger(KyroServerEncoder.class);

    /**
     * 编码出站数据
     * @param channelHandlerContext
     * @param response
     * @param byteBuf
     */
    protected void encode(ChannelHandlerContext channelHandlerContext, RpcResponse response, ByteBuf byteBuf) {
        byteBuf.writeBytes(KryoUtil.writeObjectToByteArray(response));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("[SharpRpc]: 服务器编码器捕获到异常,请查看详细的打印堆栈信息");
        cause.printStackTrace();
        ctx.close();
    }

}
