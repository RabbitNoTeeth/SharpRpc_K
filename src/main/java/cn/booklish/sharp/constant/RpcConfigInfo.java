package cn.booklish.sharp.constant;

import cn.booklish.sharp.register.ServiceScanner;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Properties;

/**
 * sharp.properties配置文件对应的属性类
 */
@Component
public class RpcConfigInfo {

    private static final Logger logger = Logger.getLogger(RpcConfigInfo.class);

    @Value("${base.zookeeper.address}")
    public String base_zk_address;

    @Value("${base.zookeeper.retryTimes}")
    public int base_zk_retryTimes;

    @Value("${base.zookeeper.sleepBetweenRetry}")
    public int base_zk_SleepBetweenRetry;

    public boolean server_enable = true;

    public int server_port = 11220;

    public boolean server_autoScan_enable;

    public String server_autoScan_base;

    public String server_service_regiter_address;

    public RpcConfigInfo(){
        loadProperties();
    }

    private void loadProperties(){
        try{
            InputStream resource = this.getClass().getClassLoader().getResourceAsStream("sharp.properties");
            Properties pop = new Properties();
            pop.load(resource);
            if(pop.get("server.enable")!=null){
                this.server_enable = Boolean.valueOf((String) pop.get("server.enable"));
            }
            if(pop.get("server.port")!=null){
                this.server_port = Integer.parseInt((String) pop.get("server.port"));
            }
            if(pop.get("server.autoScan.enable")!=null){
                this.server_autoScan_enable = Boolean.valueOf((String) pop.get("server.autoScan.enable"));
            }
            this.server_autoScan_base = (String) pop.get("server.service.autoScan.base");
            this.server_service_regiter_address = (String) pop.get("server.service.register.address");
        }catch (Exception e) {
            logger.error("加载sharp.properties配置文件失败");
            throw new RuntimeException(e);
        }

    }

}
