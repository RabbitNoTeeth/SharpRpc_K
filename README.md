# SharpRpc_K
基于Netty4的分布式rpc框架,支持redis和zookeeper,SharpRpc的Kotlin重构版本,后面该框架的更新将主要在该版本上更新


## 配置使用
**1.  java 中**

服务提供者
<pre><code>

    //创建配置类
    SharpRpcConfig sharpRpcConfig = new SharpRpcConfig();
    
    //设置注册中心
    sharpRpcConfig.getRegistry().type(RegistryCenterType.REDIS).host("47.94.206.26").port(6380);

    //设置协议
    sharpRpcConfig.getProtocol().name(ProtocolName.SHARP).host("192.168.2.246").port(12200);

    //注册服务
    sharpRpcConfig.register(Test.class,new TestImpl());

    //获取服务类
    Test testService = sharpRpcConfig.getService(Test.class);

    //使用服务类
    System.out.println(testService.run(1));

</code></pre>

服务消费者
<pre><code>

    //创建配置类
    SharpRpcConfig sharpRpcConfig = new SharpRpcConfig();
    
    //设置注册中心
    sharpRpcConfig.getRegistry().type(RegistryCenterType.REDIS).host("47.94.206.26").port(6380);

    //设置协议
    sharpRpcConfig.getProtocol().name(ProtocolName.SHARP).host("192.168.2.246").port(12200);

    //获取服务类
    Test testService = sharpRpcConfig.getService(Test.class);

    //使用服务类
    System.out.println(testService.run(1));

</code></pre>

**2. Kotlin中**

服务提供者
<pre><code>

    //创建配置类
    val sharpConfig = SharpRpcConfig()
    
    //设置注册中心
    sharpConfig.registry.type(RegistryCenterType.REDIS).host("47.94.206.26").port(6380)
    
    //设置协议
    sharpConfig.protocol.name(ProtocolName.SHARP).host("192.168.2.246").port(12200)

    //注册服务
    sharpConfig.register(Test::class.java,TestImpl())

</code></pre>

服务消费者
<pre><code>

    //创建配置类
    val sharpConfig = SharpRpcConfig()
    
    //设置注册中心
    sharpConfig.registry.type(RegistryCenterType.REDIS).host("47.94.206.26").port(6380)
    
    //设置协议
    sharpConfig.protocol.name(ProtocolName.SHARP).host("192.168.2.246").port(12200)

    //获取服务类
    val service:Test = sharpConfig.getService(Test::class.java)

    //使用服务类
    println(service.run(1))

</code></pre>


