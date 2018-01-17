package cn.booklish.sharp.test

import cn.booklish.sharp.config.SharpRpcConfig
import cn.booklish.sharp.protocol.api.ProtocolName
import cn.booklish.sharp.registry.api.RegistryCenterType
import cn.booklish.sharp.test.service.Test
import cn.booklish.sharp.test.service.TestImpl
import java.util.concurrent.CountDownLatch


// 1.首先定义一个java函数式接口


fun main(args: Array<String>) {

    val sharpConfig = SharpRpcConfig()
    sharpConfig.registry.type(RegistryCenterType.REDIS).host("47.94.206.26").port(6380)
    sharpConfig.protocol.name(ProtocolName.SHARP).host("192.168.2.246").port(12200)

    sharpConfig.register(Test::class.java,TestImpl())

    Thread.sleep(15000)

    val nThreads = 20
    val start = CountDownLatch(1)
    val end = CountDownLatch(nThreads)

    for(x in 1..nThreads){
        val thread = Thread{
            run {
                try {
                    start.await()
                    val service:Test = sharpConfig.getService(Test::class.java)
                    for(y in 1..5000){
                        println("thread-"+x+" : "+service.run(x))
                    }
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

    println("cost : ${endTime-startTime}")




}
