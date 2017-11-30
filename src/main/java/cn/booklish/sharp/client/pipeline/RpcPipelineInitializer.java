package cn.booklish.sharp.client.pipeline;

import cn.booklish.sharp.client.codec.KyoClientCodec;
import cn.booklish.sharp.client.handler.RpcResponseHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;


/**
 * @Author: liuxindong
 * @Description: Rpc客户端channelPipeline链
 * @Create: 2017/11/23 10:17
 * @Modify:
 */
public class RpcPipelineInitializer extends ChannelInitializer<SocketChannel> {

    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast(new KyoClientCodec())
                //.addLast(new WriteTimeoutHandler(15))
                .addLast(new RpcResponseHandler())
                //.addLast(new HeartBeatHandler())
        ;
    }

}
