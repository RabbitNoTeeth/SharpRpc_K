package cn.booklish.sharp.client.pipeline;

import cn.booklish.sharp.client.codec.KyoClientCodec;
import cn.booklish.sharp.client.handler.RpcResponseHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import org.apache.log4j.Logger;


/**
 * @author: 刘新冬(www.booklish.cn)
 * @date: 2017/12/2 15:26
 * @desc: Rpc客户端channelPipeline链
 */
public class RpcPipelineInitializer extends ChannelInitializer<SocketChannel> {

    private static final Logger logger = Logger.getLogger(RpcPipelineInitializer.class);

    /**
     * 初始化Channel的处理器链
     * @param socketChannel
     * @throws Exception
     */
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast(new KyoClientCodec())
                //.addLast(new WriteTimeoutHandler(15))
                .addLast(new RpcResponseHandler())
                //.addLast(new HeartBeatHandler())
        ;
        logger.info("[SharpRpc]: 客户端Channel处理器链Pipeline创建完成");
    }

}
