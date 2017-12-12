package cn.booklish.sharp.model

import java.io.Serializable

/**
 * Rpc请求消息实体
 */
data class RpcRequest(val id:Int, val serviceName:String, val methodName:String, val async:Boolean = true): Serializable{

    var paramTypes:Array<Class<*>> = emptyArray()

    var paramValues:Array<Any> = emptyArray()

}

/**
 * Rpc响应消息实体
 */
data class RpcResponse(val id:Int,
                  val result:Any? = null,
                  val success:Boolean = true,
                  val e:Exception? = null
): Serializable

