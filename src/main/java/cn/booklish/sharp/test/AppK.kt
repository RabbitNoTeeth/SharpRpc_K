package cn.booklish.sharp.test

import cn.booklish.sharp.config.SharpRpcConfig
import cn.booklish.sharp.protocol.api.ProtocolName
import cn.booklish.sharp.registry.api.RegistryCenterType
import cn.booklish.sharp.test.service.Test
import cn.booklish.sharp.test.service.TestImpl


// 1.首先定义一个java函数式接口


fun main(args: Array<String>) {

    val sharpConfig = SharpRpcConfig()
    sharpConfig.registry.type(RegistryCenterType.REDIS).host("47.94.206.26").port(6380)
    sharpConfig.protocol.name(ProtocolName.SHARP).host("192.168.2.246").port(12200)

    sharpConfig.register(Test::class.java,TestImpl())

    Thread.sleep(2000)

    val service:Test = sharpConfig.getService(Test::class.java)

    println(service.run(1))

}
