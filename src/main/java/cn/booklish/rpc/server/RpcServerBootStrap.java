package cn.booklish.rpc.server;

import cn.booklish.rpc.server.manage.RpcRegisterMap;
import cn.booklish.rpc.server.pipeline.RpcPipelineInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @Author: liuxindong
 * @Description: 服务器引导类
 * @Create: 2017/11/22 8:36
 * @Modify:
 */
public class RpcServerBootStrap {

    private final int port;

    public RpcServerBootStrap(int port){
        this.port = port;
    }

    /**
     * 注册Rpc服务
     */
    public RpcServerBootStrap register(String serviceName,Class<?> impl_type){
        RpcRegisterMap.register(serviceName,impl_type);
        return this;
    }

    /**
     * 启动Rpc服务器
     * @throws InterruptedException
     */
    public void start() throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new RpcPipelineInitializer())
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture f = b.bind(port).sync();
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }


}
