package cn.booklish.sharp.test

import cn.booklish.sharp.proxy.ServiceProxyFactory
import cn.booklish.sharp.config.SharpRpcConfig
import cn.booklish.sharp.registry.api.RegisterInfo
import cn.booklish.sharp.registry.api.RegisterTaskManager
import cn.booklish.sharp.test.service.Test
import cn.booklish.sharp.test.service.TestImpl


// 1.首先定义一个java函数式接口


fun main(args: Array<String>) {

    //创建配置类,传入服务端实现的服务类工厂
    val config = SharpRpcConfig(ServiceBeanFactory { TestImpl() })

    config.loadProperties("sharp.properties")
    //如果不使用配置文件,那么需要手动设置注册中心(如果读取了配置文件,那么手动设置覆盖配置文件)
    //config.setRegistyrCenter(RegistryCenterType.REDIS,"127.0.0.1:6379")

    //****可选操作:可以关闭服务端功能(默认启用),只使用客户端功能来获取服务代理
    //config.disableServer()
    //****可选操作:启用服务自动扫描器(默认关闭),启动后需要设置扫描的基础路径和服务的注册地址
    //config.enableAutoScanner().setAutoScanBasePath("...").setAutoScanRegisterAddress("...")

    //注册中心配置完成后,调用该方法启动sharp
    config.configure()

    //服务端注册服务
    RegisterTaskManager.submit(RegisterInfo("/test2/TestImpl","cn.booklish.sharp.test.service.TestImpl","127.0.0.1:12200"))

    //客户端获取服务
    val testService = ServiceProxyFactory.getService("/test2/TestImpl", Test::class.java) as Test

    Thread.sleep(2000)

    //客户端运行服务
    println(testService.run())

    /*val init = CountDownLatch(1)
    val end = CountDownLatch(10)

    for (x in 1..10){
        val t = Thread{
            init.await()
            try{
                println(service.run())
            }finally {
                end.countDown()
            }
        }
        t.init()
    }
    init.countDown()
    end.await()*/


}
