package cn.booklish.sharp.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * @author: 刘新冬(www.booklish.cn)
 * @data: 2017/12/4 17:11
 * @desc:
 */
public class RpcMessageUtil {

    public static ByteBuf objectToBytes(Object obj){

        ByteBuf buffer = Unpooled.buffer();
        buffer.writeBytes(KryoSerializerUtil.writeObjectToByteArray(obj));
        return buffer;

    }

    public static <T> T bytesToObject(ByteBuf buffer,Class<T> clazz){

        //获取可读字节数
        int length = buffer.readableBytes();
        //分配一个新的数组来保存具有该长度的字节数据
        byte[] bytes = new byte[length];
        //将字节复制到该数组
        buffer.readBytes(bytes);
        return KryoSerializerUtil.readObjectFromByteArray(bytes,clazz);

    }

}
