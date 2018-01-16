package cn.booklish.sharp.model

import java.io.Serializable
import java.lang.Exception


/**
 * Rpc请求消息实体
 */
class RpcRequest(val id:Int,
                      val serviceName:String,
                      val methodName:String,
                      val paramTypes:Array<Class<*>> = emptyArray(),
                      val paramValues:Array<Any> = emptyArray()
)

/**
 * Rpc响应消息实体
 */
class RpcResponse{

    var id:Int = 0

    var success:Boolean = false

    var result:Any? = null

    var error:Exception? = null

    constructor(id:Int,success:Boolean){
        this.id = id
        this.success = success
    }

    fun result(result:Any?):RpcResponse{
        this.result = result
        return this
    }

    fun error(error:Exception?):RpcResponse{
        this.error = error
        return this
    }

}

