package cn.booklish.sharp.server.pipeline;

import cn.booklish.sharp.server.codec.KyoServerCodec;
import cn.booklish.sharp.server.handler.ServerInboundHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;

/**
 * @Author: liuxindong
 * @Description: 构建Rpc的channelPipeline链
 * @Create: 2017/11/21 11:27
 * @Modify:
 */
public class ServerPipelineInitializer extends ChannelInitializer<SocketChannel>{

    protected void initChannel(SocketChannel socketChannel) throws Exception {

        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast(new KyoServerCodec())
                .addLast(new ReadTimeoutHandler(15))
                .addLast(new ServerInboundHandler(true))

        ;
    }
}
