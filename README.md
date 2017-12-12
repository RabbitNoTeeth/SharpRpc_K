# SharpRpc_K
基于Netty4的分布式rpc框架,SharpRpc的Kotlin重构版本,后面该框架的更新将主要在该版本上更新

## 重构说明
1.所有代码全部使用Kotlin重构,更加精简
2.利用Kotlin语言层对单例模式和工厂模式等的支持,重构框架内的各种管理器
3.优化配置文件的加载逻辑,简化配置流程

#### 配置使用
***1. 普通 java 项目***

*****配置 SharpRpc*****
<pre><code>
//加载配置文件(配置文件需要在classpath根路径)
SharpRpcConfig config = new SharpRpcConfig("sharp.properties",serviceBeanFactory的实现);
//自动配置
config.autoConfigure();
</code></pre>

*****使用客户端*****
<pre><code>
Test service = (Test) SharpClient.INSTANCE.getService("rpc服务的注册地址", Test.class);
</code></pre>

***2. spring 项目***

*****配置 SharpRpc*****
<pre><code>
@Configuration
public class SharpRpcAutoConfig {
    
    @Bean
    public SharpRpcConfig sharpRpcConfig(){
        SharpRpcConfig config = new SharpRpcConfig("sharp.properties",serviceBeanFactory的实现);
        config.autoConfigure();
        return config;
    }
}
</code></pre>

*****使用客户端*****
<pre><code>
Test service = (Test) SharpClient.INSTANCE.getService("rpc服务的注册地址", Test.class);
</code></pre>

***3. Kotlin项目***

*****配置 SharpRpc*****
<pre><code>
val sharpRpcConfig = SharpRpcConfig("sharp.properties"){clazz -> }
sharpRpcConfig.autoConfigure()
</code></pre>

*****使用客户端*****
<pre><code>
val service = SharpClient.getService("/test/TestImpl", Test::class.java) as Test
</code></pre>

