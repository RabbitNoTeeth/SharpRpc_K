package cn.booklish.sharp.client.pool;

import io.netty.channel.Channel;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author: 刘新冬(www.booklish.cn)
 * @date: 2017/12/2 15:27
 * @desc: Rpc客户端Channel连接池
 */
public class ClientChannelManager {

    private static AtomicReference<ClientChannelManager> instence;

    private static final Map<InetSocketAddress,ClientChannelPool> channelMap =
            new ConcurrentHashMap<>();

    private final int poolSize;

    private final int eventLoopGroupSize;

    private ClientChannelManager(int poolSize,int eventLoopGroupSize){
        this.poolSize = poolSize;
        this.eventLoopGroupSize = eventLoopGroupSize;
    }

    public static ClientChannelManager getInstence(int poolSize,int eventLoopGroupSize){
        instence.compareAndSet(null,new ClientChannelManager(poolSize,eventLoopGroupSize));
        return instence.get();
    }

    /**
     * 获取Channel连接
     * @param serverAddress
     * @return
     */
    public Channel getChannel(InetSocketAddress serverAddress){
        ClientChannelPool channelPool = channelMap.get(serverAddress);
        if(channelPool==null){
            channelMap.putIfAbsent(serverAddress,new ClientChannelPool(poolSize,eventLoopGroupSize));
        }
        return channelMap.get(serverAddress).getChannel(serverAddress);
    }


}
