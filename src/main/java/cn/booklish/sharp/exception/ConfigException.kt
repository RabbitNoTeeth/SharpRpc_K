package cn.booklish.sharp.exception

/**
 * @Author: liuxindong
 * @Description:  sharp配置文件异常基类
 * @Created: 2017/12/13 8:53
 * @Modified:
 */
class SharpConfigException:RuntimeException{
    constructor(message: String):super(message)
    constructor(message: String,cause: Throwable):super(message,cause)
}