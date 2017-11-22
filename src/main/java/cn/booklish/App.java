package cn.booklish;

import cn.booklish.rpc.server.RpcServerBootStrap;
import cn.booklish.rpc.server.pipeline.RpcPipelineInitializer;
import cn.booklish.test.TestImpl;
import cn.booklish.test.TestInterface;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Hello world!
 *
 */
public class App {

    public static void main( String[] args ) throws InterruptedException {
        new RpcServerBootStrap(9090)
                .register(TestInterface.class, TestImpl.class)
                .start();
    }

}
