# bookish-rpc-sharp
基于Netty4的分布式rpc框架,支持redis和zookeeper注册中心,SharpRpc的Kotlin重构版本,后续更新将主要在该版本上更新


## 配置使用

服务提供者
<pre><code>

    //设置注册中心
    RegistryConfig registryConfig = new RegistryConfig().type(RegistryCenterType.REDIS).host("47.94.206.26").port(6380);
    
    //设置协议(支持权重设置,服务消费者在连接提供者时,按照权重由高到低进行连接)
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


