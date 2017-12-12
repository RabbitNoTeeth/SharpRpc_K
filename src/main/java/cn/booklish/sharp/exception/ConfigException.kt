package cn.booklish.sharp.exception

/**
 * sharp配置文件异常
 */
class SharpConfigException:RuntimeException{
    constructor(message: String):super(message)
    constructor(message: String,cause: Throwable):super(message,cause)
}