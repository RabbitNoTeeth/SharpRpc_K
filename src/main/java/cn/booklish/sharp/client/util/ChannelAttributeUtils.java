package cn.booklish.sharp.client.util;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

import java.util.Map;

/**
 * @Author: liuxindong
 * @Description: 为Channel设置Attribute的工具类,主要用于在channel上存储返回值
 * @Create: 2017/11/23 15:12
 * @Modify:
 */
public class ChannelAttributeUtils {

    public static final AttributeKey<Map<Integer, Object>> KEY = AttributeKey.valueOf("dataMap");

    public static void putResponseCallback(Channel channel, Integer id, ResponseCallback callback) {
        channel.attr(KEY).get().put(id, callback);
    }

    public static ResponseCallback getResponseCallback(Channel channel, Integer id) {
        return (ResponseCallback) channel.attr(KEY).get().remove(id);
    }
}
