package cn.booklish.sharp.exception

/**
 * @Author: liuxindong
 * @Description: 检查zk节点是否存在失败的异常基类
 * @Created: 2017/12/13 8:53
 * @Modified:
 */
class CheckZkPathExistsException:RuntimeException{
    constructor(message: String):super(message)
    constructor(message: String,cause: Throwable):super(message,cause)
}

/**
 * @Author: liuxindong
 * @Description:  创建zk节点失败的异常基类
 * @Created: 2017/12/13 8:53
 * @Modified:
 */
class CreateZkPathException:RuntimeException{
    constructor(message: String):super(message)
    constructor(message: String,cause: Throwable):super(message,cause)
}

/**
 * @Author: liuxindong
 * @Description:  删除zk节点失败的异常基类
 * @Created: 2017/12/13 8:54
 * @Modified:
 */
class DeleteZkPathException:RuntimeException{
    constructor(message: String):super(message)
    constructor(message: String,cause: Throwable):super(message,cause)
}

/**
 * @Author: liuxindong
 * @Description:  获取zk节点数据失败的异常基类
 * @Created: 2017/12/13 8:54
 * @Modified:
 */
class GetZkPathDataException:RuntimeException{
    constructor(message: String):super(message)
    constructor(message: String,cause: Throwable):super(message,cause)
}

/**
 * @Author: liuxindong
 * @Description:  zk节点不存在的异常基类
 * @Created: 2017/12/13 8:54
 * @Modified:
 */
class NoSuchZkPathException:RuntimeException{
    constructor(message: String):super(message)
    constructor(message: String,cause: Throwable):super(message,cause)
}

/**
 * @Author: liuxindong
 * @Description:  更新zk节点数据失败的异常基类
 * @Created: 2017/12/13 8:54
 * @Modified:
 */
class UpdateZkPathException:RuntimeException{
    constructor(message: String):super(message)
    constructor(message: String,cause: Throwable):super(message,cause)
}