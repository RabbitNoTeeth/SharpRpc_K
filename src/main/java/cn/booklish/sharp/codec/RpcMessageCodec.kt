package cn.booklish.sharp.codec

import io.netty.channel.CombinedChannelDuplexHandler
import io.netty.handler.codec.LengthFieldBasedFrameDecoder
import io.netty.handler.codec.LengthFieldPrepender

/**
 * 客户端和服务器通用的编解码器
 *      解决普通编解码器只能最大传输接受1024字节的问题
 */
class RpcMessageCodec: CombinedChannelDuplexHandler<LengthFieldBasedFrameDecoder, LengthFieldPrepender>(
        LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,0,4,0,4)
        , LengthFieldPrepender(4, false))
