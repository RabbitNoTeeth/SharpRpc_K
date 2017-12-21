# SharpRpc_K
基于Netty4+Zookeeper的分布式rpc框架,SharpRpc的Kotlin重构版本,后面该框架的更新将主要在该版本上更新

## 重构说明
1.所有代码全部使用Kotlin重构,更加精简<br>
2.利用Kotlin语言层对单例模式和工厂模式等的支持,重构框架内的各种管理器<br>
3.优化配置文件的加载逻辑,简化配置流程

## 配置使用
***1.  java 中***

<pre><code>

    //创建配置类,传入服务端实现的服务类工厂
    SharpRpcConfig config = new SharpRpcConfig(clazz -> new TestImpl());

    config.loadProperties("sharp.properties");
    //如果不使用配置文件,那么需要手动设置注册中心(如果读取了配置文件,那么手动设置覆盖配置文件)
    //config.setRegistyrCenter(RegistryCenterType.REDIS,"127.0.0.1:6379");

    //****可选操作:可以关闭服务端功能(默认启用),只使用客户端功能来获取服务代理
    //config.disableServer();
    //****可选操作:启用服务自动扫描器(默认关闭),启动后需要设置扫描的基础路径和服务的注册地址
    //config.enableAutoScanner().setAutoScanBasePath("...").setAutoScanRegisterAddress("...");

    //注册中心配置完成后,调用该方法启动sharp
    config.configure();

    //服务端注册服务
    RegisterTaskManager.INSTANCE.submit(new RegisterInfo("/test2/TestImpl","cn.booklish.sharp.test.service.TestImpl","127.0.0.1:12200"));

    //客户端获取服务
    Test testService = (Test) ServiceProxyFactory.INSTANCE.getService("/test2/TestImpl", Test.class);

</code></pre>


***2. Kotlin中***

<pre><code>

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

</code></pre>


