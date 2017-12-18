package cn.booklish.sharp.test

import cn.booklish.sharp.proxy.ServiceProxyFactory
import cn.booklish.sharp.config.SharpRpcConfig
import cn.booklish.sharp.compute.ServiceBeanFactory
import cn.booklish.sharp.test.service.Test
import cn.booklish.sharp.test.service.TestImpl



// 1.首先定义一个java函数式接口


fun main(args: Array<String>) {

    val config = SharpRpcConfig(ServiceBeanFactory { TestImpl() })

    config.autoConfigure()

    val service = ServiceProxyFactory.getService("/test2/TestImpl", Test::class.java) as Test

    println(service.run())
}
