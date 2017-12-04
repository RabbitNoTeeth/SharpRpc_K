package cn.booklish.sharp.server.pipeline;

import cn.booklish.sharp.server.codec.ServerMessageCodec;
import cn.booklish.sharp.server.handler.DefaultServerChannelInboundHandler;
import cn.booklish.sharp.server.manage.ServerRpcRequestManager;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;

/**
 * @author: 刘新冬(www.booklish.cn)
 * @date: 2017/12/2 16:13
 * @desc: 服务器channel pipeline链
 */
public class ServerDefaultChannelInitializer extends ChannelInitializer<SocketChannel>{

    private final ServerRpcRequestManager serverRpcRequestManager;

    public ServerDefaultChannelInitializer(ServerRpcRequestManager serverRpcRequestManager) {
        this.serverRpcRequestManager = serverRpcRequestManager;
    }

    /**
     * 创建pipeline链
     * @param socketChannel
     * @throws Exception
     */
    protected void initChannel(SocketChannel socketChannel) throws Exception {

        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast(new ServerMessageCodec())
                .addLast(new ReadTimeoutHandler(15))
                .addLast(new DefaultServerChannelInboundHandler(serverRpcRequestManager,true))

        ;
    }
}
