# SharpRpc_K
基于Netty4的分布式rpc框架,支持redis和zookeeper注册中心,SharpRpc的Kotlin重构版本,后面该框架的更新将主要在该版本上更新


## 配置使用

注意<br>
1.RMI协议的实现是通过java原生RMI实现的,所以使用时要遵循java RMI规范; 同时,服务注册的RMI端口为ProtocolConfig中设置的端口port,此时服务提供者中netty的监听端口号为port+10000<br>
2.其他协议下服务注册端口和服务提供者中netty监听端口一致,都为ProtocolConfig中设置的端口port

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


