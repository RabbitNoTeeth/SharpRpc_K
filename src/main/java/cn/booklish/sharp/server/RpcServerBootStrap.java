package cn.booklish.sharp.server;

import cn.booklish.sharp.server.manage.ServerRpcRequestManager;
import cn.booklish.sharp.server.pipeline.ServerDefaultChannelInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author: 刘新冬(www.booklish.cn)
 * @date: 2017/12/2 16:14
 * @desc: 服务器引导类
 */
public class RpcServerBootStrap {

    private final EventLoopGroup bossGroup = new NioEventLoopGroup();
    private final EventLoopGroup workerGroup = new NioEventLoopGroup();
    private Channel channel;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final int port;

    private final int clientChannelTimeout;

    private final boolean asyncComputeRpcRequest;

    private final ServerRpcRequestManager serverRpcRequestManager;

    public RpcServerBootStrap(int port, int clientChannelTimeout, boolean asyncComputeRpcRequest,
                              ServerRpcRequestManager serverRpcRequestManager) {
        this.port = port;
        this.clientChannelTimeout = clientChannelTimeout;
        this.asyncComputeRpcRequest = asyncComputeRpcRequest;
        this.serverRpcRequestManager = serverRpcRequestManager;
    }


    /**
     * 启动Rpc服务器
     */
    public void start(){

        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ServerDefaultChannelInitializer(serverRpcRequestManager, clientChannelTimeout, asyncComputeRpcRequest))
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);

        executor.execute(new ServerStartTask(b,port));

    }

    /**
     * 停止服务器
     */
    public void stop(){
        if(channel!=null){
            channel.closeFuture().syncUninterruptibly();
        }
        executor.shutdown();
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
    }


    private class ServerStartTask implements Runnable{

        private final ServerBootstrap serverBootstrap;

        private final int port;

        private ServerStartTask(ServerBootstrap serverBootstrap, int port) {
            this.serverBootstrap = serverBootstrap;
            this.port = port;
        }

        @Override
        public void run() {
            try {
                ChannelFuture f = serverBootstrap.bind(port).sync();
                channel = f.channel();
                f.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                workerGroup.shutdownGracefully();
                bossGroup.shutdownGracefully();
            }
        }
    }


}
