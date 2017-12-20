package cn.booklish.sharp.remoting.netty4.util

import io.netty.channel.Channel
import java.net.InetSocketAddress


/**
 * @Author: liuxindong
 * @Description:  channel工具类
 * @Created: 2017/12/20 9:45
 * @Modified:
 */
class NettyChannelUtil {

    companion object {
        fun getRemoteAddressAsString(channel: Channel):String{
            val address = channel.remoteAddress() as InetSocketAddress
            return address.address.hostAddress + ":" + address.port
        }
    }

}