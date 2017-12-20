package cn.booklish.sharp.model

import java.io.Serializable

/**
 * @Author: liuxindong
 * @Description:  Rpc请求消息实体
 * @Created: 2017/12/13 8:56
 * @Modified:
 */
data class RpcRequest(val id:Int,
                      val serviceName:String,
                      val methodName:String,
                      val paramTypes:Array<Class<*>> = emptyArray(),
                      val paramValues:Array<Any> = emptyArray()
): Serializable

/**
 * @Author: liuxindong
 * @Description:  Rpc响应消息实体
 * @Created: 2017/12/13 8:56
 * @Modified:
 */
data class RpcResponse(val id:Int,
                  val result:Any? = null,
                  val success:Boolean = true,
                  val e:Exception? = null
): Serializable

