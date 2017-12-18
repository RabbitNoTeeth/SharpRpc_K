package cn.booklish.sharp.remoting.netty4.util

import io.netty.channel.Channel
import java.net.InetSocketAddress



class NettyChannelUtil {

    companion object {
        fun getRemoteAddressAsString(channel: Channel):String{
            val address = channel.remoteAddress() as InetSocketAddress
            return address.address.hostAddress + ":" + address.port
        }
    }

}