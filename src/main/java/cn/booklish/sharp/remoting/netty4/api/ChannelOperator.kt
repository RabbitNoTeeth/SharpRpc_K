package cn.booklish.sharp.remoting.netty4.api

import io.netty.channel.Channel

/**
 * Channel操作者
 */
interface ChannelOperator {

    fun connected(channel:Channel)

    fun disconnected(channel:Channel)

    fun send(channel:Channel,message:Any)

    fun receive(channel:Channel,message:Any)

    fun caught(channel:Channel,exception:Throwable)
}