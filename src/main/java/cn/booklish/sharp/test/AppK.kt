package cn.booklish.sharp.test

import cn.booklish.sharp.proxy.ServiceProxyFactory
import cn.booklish.sharp.config.SharpRpcConfig
import cn.booklish.sharp.compute.ServiceBeanFactory
import cn.booklish.sharp.registry.api.RegisterInfo
import cn.booklish.sharp.registry.api.RegisterTaskManager
import cn.booklish.sharp.registry.api.RegistryCenterType
import cn.booklish.sharp.test.service.Test
import cn.booklish.sharp.test.service.TestImpl
import java.util.concurrent.CountDownLatch


// 1.首先定义一个java函数式接口


fun main(args: Array<String>) {

    val config = SharpRpcConfig(ServiceBeanFactory { TestImpl() })

    config.loadProperties("sharp.properties").configure()

    RegisterTaskManager.submit(RegisterInfo("/test2/TestImpl","cn.booklish.sharp.test.service.TestImpl","127.0.0.1:12200"))

    val service = ServiceProxyFactory.getService("/test2/TestImpl", Test::class.java) as Test

    println(service.run())

    Thread.sleep(60000)

    println(service.run())

    /*val start = CountDownLatch(1)
    val end = CountDownLatch(10)

    for (x in 1..10){
        val t = Thread{
            start.await()
            try{
                println(service.run())
            }finally {
                end.countDown()
            }
        }
        t.start()
    }
    start.countDown()
    end.await()*/


}
