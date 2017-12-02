package cn.booklish.sharp.server.codec;


import cn.booklish.sharp.model.RpcRequest;
import cn.booklish.sharp.util.KryoUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * @author: 刘新冬(www.booklish.cn)
 * @date: 2017/12/2 15:55
 * @desc: 服务器解码器
 */
public class KyroServerDecoder extends ByteToMessageDecoder{

    private static final Logger logger = Logger.getLogger(KyroServerDecoder.class);

    /**
     * 解码入站数据
     * @param channelHandlerContext
     * @param byteBuf
     * @param list
     */
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

    /**
     * 异常处理
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("[SharpRpc]: 服务器解码器捕获到异常,请查看详细的打印堆栈信息");
        cause.printStackTrace();
        ctx.close();
    }

}
