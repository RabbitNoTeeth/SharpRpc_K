package cn.booklish.sharp.config;

import cn.booklish.sharp.exception.config.SharpConfigException;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * sharp.properties配置文件对应的属性类
 */
public class SharpRpcConfig {

    private static final Logger logger = Logger.getLogger(SharpRpcConfig.class);

    /**
     * 配置文件名称(配置文件必须在classpath路径下)
     */
    private final String propertiesFileName;

    /**
     * zookeeper地址
     */
    private String base_zk_address;

    /**
     * zookeeper重连次数
     */
    private int base_zk_retryTimes = 10;

    /**
     * zookeeper重连间隔
     */
    private int base_zk_SleepBetweenRetry = 5000;

    /**
     * 是否启用rpc服务器
     */
    private boolean server_enable = true;

    /**
     * rpc服务器默认监听端口
     */
    private int server_port = 11220;

    /**
     * 是否开启服务自动扫描
     */
    private boolean server_autoScan_enable;

    /**
     * 服务自动扫描的基础路径
     */
    private String server_autoScan_base;

    /**
     * 服务的注册地址
     */
    private String server_service_register_address;

    public SharpRpcConfig(String propertiesFileName){
        this.propertiesFileName = propertiesFileName;
        loadProperties(propertiesFileName);
    }

    /**
     * 加载sharp配置文件
     */
    private void loadProperties(String propertiesFileName){
        try{
            InputStream resource = this.getClass().getClassLoader().getResourceAsStream(propertiesFileName);
            Properties pop = new Properties();
            pop.load(resource);
            checkNessesaryItems(pop);
            checkServerEnable(pop);
        } catch (IOException e) {
            throw new SharpConfigException("Sharp配置文件加载失败",e);
        }

    }

    /**
     * 检查是否开启服务器配置
     * @param pop
     */
    private void checkServerEnable(Properties pop) {

        Object serverEnable = pop.get("server.enable");
        if(serverEnable !=null){
            this.server_enable = Boolean.valueOf((String) serverEnable);
            //开启rpc服务器
            if(this.server_enable){
                //开启服务器后,服务的注册地址变为必填
                Object registerAddress = pop.get("server.service.register.address");
                if(registerAddress!=null){
                    this.server_service_register_address = (String) registerAddress;
                }else{
                    throw new SharpConfigException("Sharp配置文件错误: 配置项[server.enable]为true时,配置项[server.service.register.address]不能为空");
                }
                //设置自定义端口,否则使用默认端口
                if(pop.get("server.port")!=null){
                    this.server_port = Integer.parseInt((String) pop.get("server.port"));
                }
                //判断是否开启自动扫描服务
                Object autoScanEnable = pop.get("server.autoScan.enable");
                if(autoScanEnable !=null){
                    this.server_autoScan_enable = Boolean.valueOf((String) autoScanEnable);
                    if(this.server_autoScan_enable){
                        Object autoScanBase = pop.get("server.service.autoScan.base");
                        if(autoScanBase!=null){
                            this.server_autoScan_base = (String) autoScanBase;
                        }else
                            throw new SharpConfigException("Sharp配置文件错误: 配置项[server.autoScan.enable]为true时,配置项[server.service.autoScan.base]不能为空");
                    }
                }
            }
        }

    }

    /**
     * 检查必填的zookeeper配置项
     * @param pop
     */
    private void checkNessesaryItems(Properties pop) {

        Object zkAddress = pop.get("base.zookeeper.address");
        if(zkAddress!=null)
            this.base_zk_address = (String) zkAddress;
        else
            throw new SharpConfigException("Sharp配置文件错误: 配置项[base.zookeeper.address]不能为空");


    }

    public String getBase_zk_address() {
        return base_zk_address;
    }

    public void setBase_zk_address(String base_zk_address) {
        this.base_zk_address = base_zk_address;
    }

    public int getBase_zk_retryTimes() {
        return base_zk_retryTimes;
    }

    public void setBase_zk_retryTimes(int base_zk_retryTimes) {
        this.base_zk_retryTimes = base_zk_retryTimes;
    }

    public int getBase_zk_SleepBetweenRetry() {
        return base_zk_SleepBetweenRetry;
    }

    public void setBase_zk_SleepBetweenRetry(int base_zk_SleepBetweenRetry) {
        this.base_zk_SleepBetweenRetry = base_zk_SleepBetweenRetry;
    }

    public boolean isServer_enable() {
        return server_enable;
    }

    public void setServer_enable(boolean server_enable) {
        this.server_enable = server_enable;
    }

    public int getServer_port() {
        return server_port;
    }

    public void setServer_port(int server_port) {
        this.server_port = server_port;
    }

    public boolean isServer_autoScan_enable() {
        return server_autoScan_enable;
    }

    public void setServer_autoScan_enable(boolean server_autoScan_enable) {
        this.server_autoScan_enable = server_autoScan_enable;
    }

    public String getServer_autoScan_base() {
        return server_autoScan_base;
    }

    public void setServer_autoScan_base(String server_autoScan_base) {
        this.server_autoScan_base = server_autoScan_base;
    }

    public String getServer_service_register_address() {
        return server_service_register_address;
    }

    public void setServer_service_register_address(String server_service_register_address) {
        this.server_service_register_address = server_service_register_address;
    }

    public String getPropertiesFileName() {
        return propertiesFileName;
    }
}
