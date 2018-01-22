package cn.booklish.sharp.test

import cn.booklish.sharp.config.ServiceExport
import cn.booklish.sharp.config.ServiceReference
import cn.booklish.sharp.protocol.api.ProtocolName
import cn.booklish.sharp.protocol.config.ProtocolConfig
import cn.booklish.sharp.registry.api.RegistryCenterType
import cn.booklish.sharp.registry.config.RegistryConfig
import cn.booklish.sharp.test.service.Test
import cn.booklish.sharp.test.service.TestImpl


// 1.首先定义一个java函数式接口


fun main(args: Array<String>) {

    val registryConfig = RegistryConfig().type(RegistryCenterType.REDIS).host("47.94.206.26").port(6380)
    val protocolConfig = ProtocolConfig().name(ProtocolName.SHARP).host("192.168.2.246").port(12200)

    val serviceExport = ServiceExport<Test>()

    serviceExport.setRegistry(registryConfig).setProtocol(protocolConfig).setInterface(Test::class.java)
            .setRef(TestImpl())

    serviceExport.export()

    Thread.sleep(3000)

    val serviceReference = ServiceReference<Test>()
    serviceReference.setRegistry(registryConfig).setInterface(Test::class.java)

    val test:Test = serviceReference.get()

    println(test.run(100))

    /*val nThreads = 20
    val start = CountDownLatch(1)
    val end = CountDownLatch(nThreads)

    for(x in 1..nThreads){
        val thread = Thread{
            run {
                try {
                    start.await()
                    val service:Test = sharpConfig.getService(Test::class.java)
                    println("thread-"+x+" : "+service.run(x))
                    *//*for(y in 1..5000){
                        println("thread-"+x+" : "+service.run(x))
                    }*//*
                }finally {
                    end.countDown()
                }
            }
        }
        thread.start()
    }

    val startTime = System.nanoTime()
    start.countDown()
    end.await()
    val endTime = System.nanoTime()

    println("cost : ${endTime-startTime}")*/




}
