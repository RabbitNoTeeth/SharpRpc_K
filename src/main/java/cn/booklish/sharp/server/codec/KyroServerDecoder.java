package cn.booklish.sharp.server.codec;


import cn.booklish.sharp.model.RpcRequest;
import cn.booklish.sharp.util.KryoUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @Author: liuxindong
 * @Description: Kyro解码器
 * @Create: 2017/11/21 10:53
 * @Modify:
 */
public class KyroServerDecoder extends ByteToMessageDecoder{

    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) {
        //获取可读字节数
        int length = byteBuf.readableBytes();
        //分配一个新的数组来保存具有该长度的字节数据
        byte[] bytes = new byte[length];
        //将字节复制到该数组
        byteBuf.readBytes(bytes);
        RpcRequest rpcRequest = KryoUtil.readObjectFromByteArray(bytes, RpcRequest.class);
        list.add(rpcRequest);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

}
