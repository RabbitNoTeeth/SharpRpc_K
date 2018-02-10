package `fun`.bookish.sharp.serialize.api

/**
 * @Author: liuxindong
 * @Description:  Rpc消息序列化接口,如果要更改序列化方式,只需提供该接口的实现即可
 * @Created: 2017/12/20 9:45
 * @Modified:
 */
interface RpcSerializer {

    fun serialize(obj: Any):ByteArray

    fun deserialize(byteArray: ByteArray):Any

}