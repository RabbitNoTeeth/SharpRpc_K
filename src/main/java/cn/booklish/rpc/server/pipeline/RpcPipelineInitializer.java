package cn.booklish.rpc.server.pipeline;

import cn.booklish.rpc.server.codec.KyroDecoder;
import cn.booklish.rpc.server.codec.KyroEncoder;
import cn.booklish.rpc.server.handler.RpcInboundHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

/**
 * @Author: liuxindong
 * @Description: 构建Rpc的channelPipeline链
 * @Create: 2017/11/21 11:27
 * @Modify:
 */
public class RpcPipelineInitializer extends ChannelInitializer<SocketChannel>{

    protected void initChannel(SocketChannel socketChannel) throws Exception {

        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast(new KyroDecoder())                     //添加解码器
                .addLast(new KyroEncoder())                     //添加编码器
                .addLast(new RpcInboundHandler()                //添加入站处理器
                );
    }
}
