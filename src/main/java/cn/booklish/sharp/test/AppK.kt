package cn.booklish.sharp.test

import cn.booklish.sharp.client.SharpClient
import cn.booklish.sharp.config.SharpRpcConfig
import cn.booklish.sharp.server.compute.ServiceBeanFactory
import cn.booklish.sharp.test.service.Test
import cn.booklish.sharp.test.service.TestImpl


fun main(args: Array<String>) {
    val config = SharpRpcConfig("sharp.properties", { clazz -> TestImpl() } as ServiceBeanFactory)

    config.autoConfigure()

    val service = SharpClient.getService("/test/TestImpl", Test::class.java) as Test
}
