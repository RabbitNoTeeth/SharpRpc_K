package cn.booklish.sharp.config.exception

/**
 * sharp配置文件异常基类
 */
class SharpConfigException:RuntimeException{
    constructor(message: String):super(message)
    constructor(message: String,cause: Throwable):super(message,cause)
}