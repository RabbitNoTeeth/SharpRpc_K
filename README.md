# SharpRpc_K
基于Netty4的分布式rpc框架,支持redis和zookeeper注册中心,SharpRpc的Kotlin重构版本,后面该框架的更新将主要在该版本上更新


## 配置使用

注意<br>
1.RMI协议的实现是通过java原生RMI实现的,所以使用时要遵循java RMI规范; 同时,服务注册的RMI端口为ProtocolConfig中设置的端口port,此时服务提供者中netty的监听端口号为port+10000<br>
2.其他协议下服务注册端口和服务提供者中netty监听端口一致,都为ProtocolConfig中设置的端口port

**1.  java 中**

服务提供者
<pre><code>

    //设置注册中心
    RegistryConfig registryConfig = new RegistryConfig().type(RegistryCenterType.REDIS).host("47.94.206.26").port(6380);
    
    //设置协议
    ProtocolConfig protocolConfig = new ProtocolConfig().name(ProtocolName.SHARP).host("192.168.2.246").port(12200);

    //创建提供者配置
    ServiceExport<Test> serviceExport = new ServiceExport<>();

    //设置服务的注册中心,协议,接口及实现类(支持多协议和多注册中心)
    serviceExport.setRegistry(registryConfig).setProtocol(protocolConfig).setInterface(Test.class).setRef(new TestImpl());

    //注册并暴露服务
    serviceExport.export();

</code></pre>

服务消费者
<pre><code>

    //设置注册中心
    RegistryConfig registryConfig = new RegistryConfig().type(RegistryCenterType.REDIS).host("47.94.206.26").port(6380);

    //创建服务引用配置
    ServiceReference<Test> serviceReference = new ServiceReference<>();

    //设置服务注册中心和接口
    serviceReference.setRegistry(registryConfig).setInterface(Test.class);

    //获取服务
    Test test = serviceReference.get();

    //使用服务
    System.out.println(test.run(100));

</code></pre>

**2. Kotlin中**

服务提供者
<pre><code>

    //设置注册中心
    val registryConfig = RegistryConfig().type(RegistryCenterType.REDIS).host("47.94.206.26").port(6380)
    
    //设置协议
    val protocolConfig = ProtocolConfig().name(ProtocolName.SHARP).host("192.168.2.246").port(12200)

    //创建服务提供者配置
    val serviceExport = ServiceExport<Test>()

    //设置注册中心和协议(支持多注册中心和多协议),设置服务接口和服务实现
    serviceExport.setRegistry(registryConfig).setProtocol(protocolConfig).setInterface(Test::class.java)
            .setRef(TestImpl())

    //注册并暴露服务
    serviceExport.export()

</code></pre>

服务消费者
<pre><code>
    
    //设置注册中心
    val registryConfig = RegistryConfig().type(RegistryCenterType.REDIS).host("47.94.206.26").port(6380)
    
    //创建服务引用配置
    val serviceReference = ServiceReference<Test>()
    
    //设置注册中心和服务接口
    serviceReference.setRegistry(registryConfig).setInterface(Test::class.java)

    //获取服务
    val test:Test = serviceReference.get()
    
    //使用服务
    println(test.run(100))

</code></pre>


