package cn.booklish.sharp.test

import cn.booklish.sharp.proxy.ServiceProxyFactory
import cn.booklish.sharp.config.SharpRpcConfig
import cn.booklish.sharp.compute.ServiceBeanFactory
import cn.booklish.sharp.registry.api.RegisterInfo
import cn.booklish.sharp.registry.api.RegisterTaskManager
import cn.booklish.sharp.registry.api.RegistryCenterType
import cn.booklish.sharp.test.service.Test
import cn.booklish.sharp.test.service.TestImpl



// 1.首先定义一个java函数式接口


fun main(args: Array<String>) {

    val config = SharpRpcConfig(ServiceBeanFactory { TestImpl() })

    config.setRegistyrCenter(RegistryCenterType.ZOOKEEPER,"47.94.206.26:2181").configure()

    RegisterTaskManager.submit(RegisterInfo("/test2/TestImpl","cn.booklish.sharp.test.service.TestImpl","127.0.0.1:12200"))

    Thread.sleep(3000)

    val service = ServiceProxyFactory.getService("/test2/TestImpl", Test::class.java) as Test

    for (x in 1..10){
        println(service.run())
    }

}
