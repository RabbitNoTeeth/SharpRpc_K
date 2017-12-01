package cn.booklish.test.autoconfig;

import cn.booklish.sharp.config.SharpAutoConfigureCenter;
import cn.booklish.sharp.config.SharpRpcConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * JavaConfig
 */
@Configuration
public class RpcAutoConfig {

    @Bean
    public SharpRpcConfig sharpRpcConfig(){
        return new SharpRpcConfig("sharp.properties");
    }

    @Bean
    public SharpAutoConfigureCenter sharpAutoConfigureCenter(SharpRpcConfig sharpRpcConfig){
        SharpAutoConfigureCenter center = new SharpAutoConfigureCenter(sharpRpcConfig);
        center.autoConfigure();
        return center;
    }





}
