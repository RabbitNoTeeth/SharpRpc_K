package `fun`.bookish.sharp.remoting.netty4.codec

import `fun`.bookish.sharp.serialize.api.RpcSerializer
import io.netty.channel.CombinedChannelDuplexHandler
import io.netty.handler.codec.LengthFieldBasedFrameDecoder
import io.netty.handler.codec.LengthFieldPrepender

/**
 * @Author: liuxindong
 * @Description:  客户端和服务器通用的编解码器(结合自定义实现的编解码器)
 * @Created: 2017/12/13 8:50
 * @Modified:
 */
class MessageCodec(rpcSerializer: RpcSerializer) :
        CombinedChannelDuplexHandler<MessageDecoder, MessageEncoder>(MessageDecoder(rpcSerializer), MessageEncoder(rpcSerializer))

/**
 * @Author: liuxindong
 * @Description:  客户端和服务器通用的编解码器(结合netty提供的基于长度的编解码器)
 * @Created: 2017/12/13 8:50
 * @Modified:
 */
class MessageCodec2 : CombinedChannelDuplexHandler<LengthFieldBasedFrameDecoder, LengthFieldPrepender>(
        LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,0,4,0,4)
        , LengthFieldPrepender(4, false))
