package cn.booklish.sharp.serialize.api


interface RpcSerializer {

    fun serialize(obj: Any):ByteArray

    fun deserialize(byteArray: ByteArray):Any

}