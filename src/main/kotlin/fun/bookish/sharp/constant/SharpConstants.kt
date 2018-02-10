package `fun`.bookish.sharp.constant

import `fun`.bookish.sharp.protocol.api.ProtocolName
import `fun`.bookish.sharp.registry.api.RegistryCenterType

/**
 * Sharp配置默认值常量
 */
object SharpConstants {


    /*--------------------------默认registry注册中心配置--------------------------*/

    var DEFAULT_REGISTRY_TYPE = RegistryCenterType.REDIS

    var DEFAULT_REGISTRY_HOST = "127.0.0.1"

    var DEFAULT_REGISTRY_PORT = 6379

    var DEFAULT_REGISTRY_TIMEOUT = 30


    /*--------------------------默认protocol配置--------------------------*/

    var DEFAULT_PROTOCOL_NAME = ProtocolName.RMI

    var DEFAULT_PROTOCOL_HOST = ""

    var DEFAULT_PROTOCOL_PORT = 12200

    var DEFAULT_PROTOCOL_WEIGHT = 1

}