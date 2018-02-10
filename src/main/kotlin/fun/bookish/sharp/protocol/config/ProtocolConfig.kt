package `fun`.bookish.sharp.protocol.config

import `fun`.bookish.sharp.constant.SharpConstants
import `fun`.bookish.sharp.protocol.api.ProtocolName

/**
 * rpc协议配置类
 */
class ProtocolConfig {

    var name = SharpConstants.DEFAULT_PROTOCOL_NAME

    var host = SharpConstants.DEFAULT_PROTOCOL_HOST

    var port = SharpConstants.DEFAULT_PROTOCOL_PORT

    var weight = SharpConstants.DEFAULT_PROTOCOL_WEIGHT

    fun name(name:ProtocolName):ProtocolConfig{
        this.name = name
        return this
    }

    fun host(host:String):ProtocolConfig{
        this.host = host
        return this
    }

    fun port(port:Int):ProtocolConfig{
        this.port = port
        return this
    }

    fun weight(weight:Int):ProtocolConfig{
        this.weight = weight
        return this
    }

}