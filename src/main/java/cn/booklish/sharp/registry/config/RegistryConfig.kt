package cn.booklish.sharp.registry.config

import cn.booklish.sharp.constant.SharpConstants
import cn.booklish.sharp.registry.api.RegistryCenter

/**
 * 注册中心配置类
 */
class RegistryConfig {

    var type = SharpConstants.DEFAULT_REGISTRY_TYPE

    var address  = SharpConstants.DEFAULT_REGISTRY_ADDRESS

    var port = SharpConstants.DEFAULT_REGISTRY_PORT

    var timeout = SharpConstants.DEFAULT_REGISTRY_TIMEOUT

    var registryCenter: RegistryCenter? = null

}