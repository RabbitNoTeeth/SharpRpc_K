package cn.booklish.sharp.client.pool;

import io.netty.channel.Channel;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: liuxindong
 * @Description: Rpc客户端Channel连接池
 * @Create: 2017/11/23 10:40
 * @Modify:
 */
public class RpcClientChannelManager {

    private static final Map<InetSocketAddress,RpcClientChannelPool> channelMap =
            new ConcurrentHashMap<>();

    public static Channel getChannel(InetSocketAddress serverAddress){
        RpcClientChannelPool channelPool = channelMap.get(serverAddress);
        if(channelPool==null){
            channelMap.putIfAbsent(serverAddress,new RpcClientChannelPool(20));
        }
        return channelMap.get(serverAddress).getChannel(serverAddress);
    }


}
