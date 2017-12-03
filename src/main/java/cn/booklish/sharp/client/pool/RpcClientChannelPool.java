package cn.booklish.sharp.client.pool;

import cn.booklish.sharp.client.pipeline.DefaultClientChannelInitializer;
import cn.booklish.sharp.client.util.ChannelAttributeUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.Attribute;
import org.apache.log4j.Logger;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

/**
 * @author: 刘新冬(www.booklish.cn)
 * @date: 2017/12/2 15:37
 * @desc: 客户端Channel连接池
 */
public class RpcClientChannelPool {

    private static final Logger logger = Logger.getLogger(RpcClientChannelPool.class);

    private final int capacity;

    private final Channel[] channelBucket;

    private final Object[] lockBucket;

    /**
     * Bootstrap共享的EventLoopGroup,因为EventLoopGroup管理一组EventLoop,每个EventLoop都是一个
     * 线程,EventLoopGroup相当于Channel的一个共享线程池.
     * 通过共享EventLoopGroup,达到多个代理调用共享Channel,多个Channel共享EventLoop的目的.
     * 后期可以通过调整EventLoopGroup的大小来管理其内部线程数量,来实现更灵活的伸缩性能
     */
    private static final EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

    public RpcClientChannelPool(int capacity) {
        this.capacity = capacity;
        channelBucket = new Channel[capacity];
        lockBucket = new Object[capacity];
        for(int x=0;x<capacity;x++){
            lockBucket[x] = new Object();
        }
    }

    /**
     * 从Channel连接池中获取Channel连接
     * @param address
     * @return
     */
    public Channel getChannel(InetSocketAddress address){

        int index = new Random().nextInt(capacity);
        Channel channel = channelBucket[index];
        if(channel!=null && channel.isActive()){
            return channel;
        }
        synchronized (lockBucket[index]){
            channel = channelBucket[index];
            if (channel != null && channel.isActive()) {
                return channel;
            }
            channel = addNewChannelToPool(address);
            channelBucket[index] = channel;
            return channel;
        }
    }

    /**
     * 创建新的Channel连接并添加到连接池
     *   连接失败后自动重试,但是有限定的重试次数
     * @param address
     * @return
     */
    private Channel addNewChannelToPool(InetSocketAddress address) {

        Bootstrap bootstrap = new Bootstrap();
        //设置信号量,最多允许重试3次
        Semaphore semaphore = new Semaphore(3);
        do {
            try{
                if(semaphore.tryAcquire()){
                    bootstrap.group(eventLoopGroup)
                            .channel(NioSocketChannel.class)
                            .option(ChannelOption.SO_KEEPALIVE, Boolean.TRUE)
                            .option(ChannelOption.TCP_NODELAY, Boolean.TRUE)
                            .handler(new DefaultClientChannelInitializer());
                    ChannelFuture channelFuture = bootstrap.connect(address).sync();
                    Channel channel = channelFuture.channel();
                    //为刚刚创建的channel，初始化channel属性
                    Attribute<Map<Integer,Object>> attribute = channel.attr(ChannelAttributeUtils.KEY);
                    ConcurrentHashMap<Integer, Object> dataMap = new ConcurrentHashMap<>();
                    attribute.set(dataMap);
                    return channel;
                }else{
                    return null;
                }
            }catch (InterruptedException e){
                Thread.currentThread().interrupt();
                //重试
                logger.info("[SharpRpc]: 客户端channel连接失败,重新尝试连接...");
            } catch (Exception e) {
                e.printStackTrace();
                //重试
                logger.info("[SharpRpc]: 客户端channel连接失败,重新尝试连接...");
            }
        }while (true);
    }

}
