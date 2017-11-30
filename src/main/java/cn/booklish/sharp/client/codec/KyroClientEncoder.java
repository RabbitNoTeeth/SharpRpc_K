package cn.booklish.sharp.client.codec;

import cn.booklish.sharp.model.RpcRequest;
import cn.booklish.sharp.util.KryoUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @Author: liuxindong
 * @Description: 编码器
 * @Create: 2017/11/23 10:19
 * @Modify:
 */
public class KyroClientEncoder extends MessageToByteEncoder<RpcRequest> {

    protected void encode(ChannelHandlerContext channelHandlerContext, RpcRequest rpcRequest, ByteBuf byteBuf) {
        byteBuf.writeBytes(KryoUtil.writeObjectToByteArray(rpcRequest));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

}
