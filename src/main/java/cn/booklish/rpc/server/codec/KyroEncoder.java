package cn.booklish.rpc.server.codec;


import cn.booklish.rpc.server.util.KryoUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @Author: liuxindong
 * @Description: Kyro编码器
 * @Create: 2017/11/21 11:16
 * @Modify:
 */
public class KyroEncoder extends MessageToByteEncoder<Object>{

    protected void encode(ChannelHandlerContext channelHandlerContext, Object obj, ByteBuf byteBuf) {
        byte[] bytes = KryoUtil.writeToByteArray(obj);
        byteBuf.writeBytes(bytes);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

}
