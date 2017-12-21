package cn.booklish.sharp.registry.exception

/**
 * @Author: liuxindong
 * @Description: 检查服务是否存在失败的异常基类
 * @Created: 2017/12/13 8:53
 * @Modified:
 */
class CheckPathExistsException:RuntimeException{
    constructor(message: String):super(message)
    constructor(message: String,cause: Throwable):super(message,cause)
}

/**
 * @Author: liuxindong
 * @Description:  注册服务失败的异常基类
 * @Created: 2017/12/13 8:53
 * @Modified:
 */
class CreatePathException:RuntimeException{
    constructor(message: String):super(message)
    constructor(message: String,cause: Throwable):super(message,cause)
}

/**
 * @Author: liuxindong
 * @Description:  删除服务失败的异常基类
 * @Created: 2017/12/13 8:54
 * @Modified:
 */
class DeletePathException:RuntimeException{
    constructor(message: String):super(message)
    constructor(message: String,cause: Throwable):super(message,cause)
}

/**
 * @Author: liuxindong
 * @Description:  获取服务数据失败的异常基类
 * @Created: 2017/12/13 8:54
 * @Modified:
 */
class GetPathDataException:RuntimeException{
    constructor(message: String):super(message)
    constructor(message: String,cause: Throwable):super(message,cause)
}

/**
 * @Author: liuxindong
 * @Description:  服务不存在的异常基类
 * @Created: 2017/12/13 8:54
 * @Modified:
 */
class NoSuchPathException:RuntimeException{
    constructor(message: String):super(message)
    constructor(message: String,cause: Throwable):super(message,cause)
}

/**
 * @Author: liuxindong
 * @Description:  更新服务数据失败的异常基类
 * @Created: 2017/12/13 8:54
 * @Modified:
 */
class UpdatePathException:RuntimeException{
    constructor(message: String):super(message)
    constructor(message: String,cause: Throwable):super(message,cause)
}

/**
 * @Author: liuxindong
 * @Description:  获取子服务失败的异常基类
 * @Created: 2017/12/13 8:54
 * @Modified:
 */
class GetChildrenPathException:RuntimeException{
    constructor(message: String):super(message)
    constructor(message: String,cause: Throwable):super(message,cause)
}