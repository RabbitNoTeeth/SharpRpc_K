package cn.booklish.sharp.client.pipeline;

import cn.booklish.sharp.client.codec.ClientMessageCodec;
import cn.booklish.sharp.client.handler.DefaultClientChannelInboundHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import org.apache.log4j.Logger;


/**
 * @author: 刘新冬(www.booklish.cn)
 * @date: 2017/12/2 15:26
 * @desc: Rpc客户端channelPipeline链
 */
public class DefaultClientChannelInitializer extends ChannelInitializer<SocketChannel> {

    private static final Logger logger = Logger.getLogger(DefaultClientChannelInitializer.class);

    /**
     * 初始化Channel的处理器链
     * @param socketChannel
     * @throws Exception
     */
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast(new ClientMessageCodec())
                .addLast(new DefaultClientChannelInboundHandler())
                //.addLast(new HeartBeatHandler())
        ;
        logger.info("[SharpRpc]: 客户端Channel处理器链Pipeline创建完成");
    }

}
