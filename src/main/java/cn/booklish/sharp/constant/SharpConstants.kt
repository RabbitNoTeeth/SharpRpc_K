package cn.booklish.sharp.constant

import cn.booklish.sharp.protocol.api.ProtocolName
import cn.booklish.sharp.registry.api.RegistryCenterType
import cn.booklish.sharp.remoting.netty4.api.ClientChannelOperator
import cn.booklish.sharp.remoting.netty4.api.ServerChannelOperator
import cn.booklish.sharp.serialize.kryo.KryoSerializer
import kotlin.math.min

/**
 * Sharp配置默认值常量
 */
object SharpConstants {

    /*--------------------------默认序列化配置--------------------------*/

    val DEFAULT_SERIALIZER = KryoSerializer()


    /*--------------------------默认registry注册中心配置--------------------------*/

    val DEFAULT_REGISTRY_TYPE = RegistryCenterType.REDIS

    var DEFAULT_REGISTRY_HOST = "127.0.0.1"

    var DEFAULT_REGISTRY_PORT = 6379

    var DEFAULT_REGISTRY_TIMEOUT = 30


    /*--------------------------默认protocol配置--------------------------*/

    val DEFAULT_PROTOCOL_NAME = ProtocolName.RMI

    val DEFAULT_PROTOCOL_HOST = ""

    val DEFAULT_PROTOCOL_PORT = 12200


    /*--------------------------默认client配置--------------------------*/

    val DEFAULT_CLIENT_CHANNEL_OPERATOR = ClientChannelOperator()

    /*--------------------------默认server配置--------------------------*/

    val DEFAULT_SERVER_CHANNEL_OPERATOR = ServerChannelOperator()

    val DEFAULT_SERVER_COMPUTE_THREAD_POOL_SIZE = min(Runtime.getRuntime().availableProcessors()+1,32)


}