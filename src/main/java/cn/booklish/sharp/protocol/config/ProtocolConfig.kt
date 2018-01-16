package cn.booklish.sharp.protocol.config

import cn.booklish.sharp.constant.SharpConstants
import cn.booklish.sharp.protocol.api.ProtocolName

/**
 * rpc协议配置类
 */
class ProtocolConfig {

    var name = SharpConstants.DEFAULT_PROTOCOL_NAME

    var host = SharpConstants.DEFAULT_PROTOCOL_HOST

    var port = SharpConstants.DEFAULT_PROTOCOL_PORT

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

}