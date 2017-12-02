package cn.booklish.sharp.server.pipeline;

import cn.booklish.sharp.server.codec.KyoServerCodec;
import cn.booklish.sharp.server.handler.ServerInboundHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;

/**
 * @author: 刘新冬(www.booklish.cn)
 * @date: 2017/12/2 16:13
 * @desc: 服务器channel pipeline链
 */
public class ServerPipelineInitializer extends ChannelInitializer<SocketChannel>{

    /**
     * 创建pipeline链
     * @param socketChannel
     * @throws Exception
     */
    protected void initChannel(SocketChannel socketChannel) throws Exception {

        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast(new KyoServerCodec())
                .addLast(new ReadTimeoutHandler(15))
                .addLast(new ServerInboundHandler(true))

        ;
    }
}
