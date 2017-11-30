package cn.booklish.sharp.autoconfig;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.net.MalformedURLException;

/**
 * JavaConfig
 */
@Configuration
public class RpcAutoConfig {


    /**
     * 加载Sharp配置文件
     * @return
     * @throws MalformedURLException
     */
    @Bean
    public static PropertyPlaceholderConfigurer propertyPlaceholderConfigurer() throws MalformedURLException {
        PropertyPlaceholderConfigurer configurer = new PropertyPlaceholderConfigurer();
        configurer.setLocation(new ClassPathResource("sharp.properties"));
        return configurer;
    }




}
