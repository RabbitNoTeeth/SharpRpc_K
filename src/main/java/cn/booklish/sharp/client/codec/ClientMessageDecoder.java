package cn.booklish.sharp.client.codec;

import cn.booklish.sharp.model.RpcResponse;
import cn.booklish.sharp.util.KryoSerializerUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * @author: 刘新冬(www.booklish.cn)
 * @date: 2017/12/2 14:48
 * @desc: 客户端解码器
 */
public class ClientMessageDecoder extends ByteToMessageDecoder {

    private static final Logger logger = Logger.getLogger(ClientMessageDecoder.class);

    /**
     * 解码入站数据
     * @param channelHandlerContext
     * @param byteBuf
     * @param list
     */
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list){

        // 获取可读字节数
        int length = byteBuf.readableBytes();
        // 分配一个新的数组来保存具有该长度的字节数据
        byte[] bytes = new byte[length];
        // 将字节复制到该数组
        byteBuf.readBytes(bytes);
        // 将字节反序列化为Rpc响应实体
        RpcResponse rpcRequest = KryoSerializerUtil.readObjectFromByteArray(bytes, RpcResponse.class);

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
        logger.error("[SharpRpc]: 客户端解码器捕获到异常,请查看详细的打印堆栈信息");
        cause.printStackTrace();
        ctx.close();
    }
}