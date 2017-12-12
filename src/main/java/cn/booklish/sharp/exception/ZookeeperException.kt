package cn.booklish.sharp.exception

/**
 * 检查zk节点是否存在失败异常
 */
class CheckZkPathExistsException:RuntimeException{
    constructor(message: String):super(message)
    constructor(message: String,cause: Throwable):super(message,cause)
}

/**
 * 创建zk节点失败异常
 */
class CreateZkPathException:RuntimeException{
    constructor(message: String):super(message)
    constructor(message: String,cause: Throwable):super(message,cause)
}

/**
 * 删除zk节点失败异常
 */
class DeleteZkPathException:RuntimeException{
    constructor(message: String):super(message)
    constructor(message: String,cause: Throwable):super(message,cause)
}

/**
 * 获取zk节点数据失败异常
 */
class GetZkPathDataException:RuntimeException{
    constructor(message: String):super(message)
    constructor(message: String,cause: Throwable):super(message,cause)
}

/**
 * zk节点不存在异常
 */
class NoSuchZkPathException:RuntimeException{
    constructor(message: String):super(message)
    constructor(message: String,cause: Throwable):super(message,cause)
}

/**
 * 更新zk节点数据失败异常
 */
class UpdateZkPathException:RuntimeException{
    constructor(message: String):super(message)
    constructor(message: String,cause: Throwable):super(message,cause)
}