# SharpRpc
基于Netty4的分布式rpc框架

#### 配置使用
***1. 普通java项目***

*****配置SharpRpc*****
<pre><code>
//加载配置文件(配置文件需要在classpath根路径)
SharpRpcConfig sharpRpcConfig = SharpRpcConfig.getInstance().load("sharp.properties");
//创建配置中心
SharpAutoConfigureCenter configureCenter = new SharpAutoConfigureCenter(sharpRpcConfig, new ServiceBeanFactory() {...});
//开启自动配置
configureCenter.autoConfigure();
</code></pre>

*****使用客户端*****
<pre><code>
ArticleClassifyService service = (ArticleClassifyService) sharpAutoConfigureCenter.getSharpClient()
                .getService("rpc服务的注册地址", ArticleClassifyService.class);
</code></pre>

***2. spring项目***

*****配置SharpRpc*****
<pre><code>
@Configuration
public class SharpRpcAutoConfig {
    /**
     * 自动创建注入sharp配置
     */
    @Bean
    public SharpRpcConfig sharpRpcConfig(){
        return SharpRpcConfig.getInstance().load("sharp.properties");
    }
    /**
     * 创建配置中心并启动自动配置
     */
    @Bean
    public SharpAutoConfigureCenter sharpAutoConfigureCenter(SharpRpcConfig sharpRpcConfig){
        SharpAutoConfigureCenter configureCenter = new SharpAutoConfigureCenter(sharpRpcConfig, new ServiceBeanFactory() {...});
        configureCenter.autoConfigure();
        return configureCenter;
    }

}
</code></pre>

*****使用客户端*****
<pre><code>
ArticleClassifyService service = (ArticleClassifyService) sharpAutoConfigureCenter.getSharpClient()
                .getService("rpc服务的注册地址", ArticleClassifyService.class);
</code></pre>


