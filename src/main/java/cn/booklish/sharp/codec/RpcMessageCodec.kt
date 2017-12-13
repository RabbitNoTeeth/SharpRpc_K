package cn.booklish.sharp.codec

import io.netty.channel.CombinedChannelDuplexHandler
import io.netty.handler.codec.LengthFieldBasedFrameDecoder
import io.netty.handler.codec.LengthFieldPrepender

/**
 * @Author: liuxindong
 * @Description:  客户端和服务器通用的编解码器,解决普通编解码器只能最大传输和接收1024字节的问题
 * @Created: 2017/12/13 8:50
 * @Modified:
 */
class RpcMessageCodec: CombinedChannelDuplexHandler<LengthFieldBasedFrameDecoder, LengthFieldPrepender>(
        LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,0,4,0,4)
        , LengthFieldPrepender(4, false))
