package cn.booklish.sharp.client.pool;

import io.netty.channel.Channel;
import org.apache.log4j.Logger;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: 刘新冬(www.booklish.cn)
 * @date: 2017/12/2 15:27
 * @desc: Rpc客户端Channel连接池
 */
public class RpcClientChannelManager {

    private static final Map<InetSocketAddress,RpcClientChannelPool> channelMap =
            new ConcurrentHashMap<>();

    /**
     * 获取Channel连接
     * @param serverAddress
     * @return
     */
    public static Channel getChannel(InetSocketAddress serverAddress){
        RpcClientChannelPool channelPool = channelMap.get(serverAddress);
        if(channelPool==null){
            channelMap.putIfAbsent(serverAddress,new RpcClientChannelPool(20));
        }
        return channelMap.get(serverAddress).getChannel(serverAddress);
    }


}
