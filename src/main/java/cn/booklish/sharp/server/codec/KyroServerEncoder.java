package cn.booklish.sharp.server.codec;


import cn.booklish.sharp.model.RpcResponse;
import cn.booklish.sharp.util.KryoUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @Author: liuxindong
 * @Description: Kyro编码器
 * @Create: 2017/11/21 11:16
 * @Modify:
 */
public class KyroServerEncoder extends MessageToByteEncoder<RpcResponse>{

    protected void encode(ChannelHandlerContext channelHandlerContext, RpcResponse response, ByteBuf byteBuf) {
        byteBuf.writeBytes(KryoUtil.writeObjectToByteArray(response));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

}
