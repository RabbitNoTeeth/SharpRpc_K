package cn.booklish.sharp.test

import cn.booklish.sharp.client.SharpClient
import cn.booklish.sharp.config.SharpRpcConfig
import cn.booklish.sharp.server.compute.ServiceBeanFactory
import cn.booklish.sharp.test.service.Test
import cn.booklish.sharp.test.service.TestImpl



// 1.首先定义一个java函数式接口


fun main(args: Array<String>) {

    val config = SharpRpcConfig("sharp.properties",ServiceBeanFactory { clazz -> TestImpl()})

    config.autoConfigure()

    val service = SharpClient.getService("/test/TestImpl", Test::class.java) as Test

    println(service.run())
}
