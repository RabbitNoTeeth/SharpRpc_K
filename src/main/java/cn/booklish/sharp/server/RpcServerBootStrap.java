package cn.booklish.sharp.server;

import cn.booklish.sharp.constant.RpcConfigInfo;
import cn.booklish.sharp.server.pipeline.ServerPipelineInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author: liuxindong
 * @Description: 服务器引导类
 * @Create: 2017/11/22 8:36
 * @Modify:
 */
@Component
public class RpcServerBootStrap {

    private final EventLoopGroup bossGroup = new NioEventLoopGroup();
    private final EventLoopGroup workerGroup = new NioEventLoopGroup();
    private Channel channel;

    private final RpcConfigInfo rpcConfigInfo;

    @Autowired
    public RpcServerBootStrap(RpcConfigInfo rpcConfigInfo) {
        this.rpcConfigInfo = rpcConfigInfo;
        if(rpcConfigInfo.server_enable){
            start(rpcConfigInfo.server_port);
        }
    }


    /**
     * 启动Rpc服务器
     */
    public void start(int port){

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ServerPipelineInitializer())
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture f = b.bind(port).sync();
            channel = f.channel();
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }

    }

    public void stop(){
        if(channel!=null){
            channel.closeFuture().syncUninterruptibly();
        }
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
    }


}
