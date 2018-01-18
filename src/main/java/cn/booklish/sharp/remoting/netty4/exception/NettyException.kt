package cn.booklish.sharp.remoting.netty4.exception

/**
 * channel连接失败异常
 */
class ChannelConnectException:RuntimeException{
    constructor(cause: Throwable):super(cause)
    constructor(message: String):super(message)
    constructor(message: String,cause: Throwable):super(message,cause)
}