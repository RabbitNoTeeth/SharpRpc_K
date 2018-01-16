package cn.booklish.sharp.remoting.netty4.util

import io.netty.channel.Channel
import java.net.InetSocketAddress


/**
 * channel工具类
 */
object NettyUtil {

    fun getRemoteAddressAsString(channel: Channel): String{
        val address = channel.remoteAddress() as InetSocketAddress
        return address.address.hostAddress + ":" + address.port
    }

    fun resolveAddressString(address:String): InetSocketAddress{
        val list = address.split(":")
        return InetSocketAddress(list[0],list[1].toInt())
    }

}