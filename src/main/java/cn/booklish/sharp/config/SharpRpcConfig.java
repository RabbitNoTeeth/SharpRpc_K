package cn.booklish.sharp.config;

import cn.booklish.sharp.exception.config.SharpConfigException;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author: 刘新冬(www.booklish.cn)
 * @date: 2017/12/2 15:44
 * @desc: SharpRpc配置文件属性bean
 */
public class SharpRpcConfig {

    private static final Logger logger = Logger.getLogger(SharpRpcConfig.class);

    private static final SharpRpcConfig instance = new SharpRpcConfig();

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
     * zookeeper连接池大小（默认为15）
     */
    private int base_zk_poolSize = 15;

    /**
     * 是否启用rpc服务器
     */
    private boolean server_enable = true;

    /**
     * rpc服务器默认监听端口
     */
    private int server_port = 11220;

    /**
     * 服务器eventLoopGroup初始大小
     */
    private int server_eventLoopGroup_size = 0;

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

    /**
     * 服务器是否异步处理Rpc请求
     */
    private Boolean server_compute_async = true;

    /**
     * 服务器异步处理Rpc请求的线程池大小
     */
    private int server_compute_poolSize = 2;

    /**
     * 客户端连接池大小
     */
    private int client_channel_poolSize = 15;

    /**
     * 客户端eventLoopGroup初始大小（0为不设置，采用netty默认值）
     */
    private int client_eventLoopGroup_size = 0;

    /**
     * 客户端channel连接过期时间大小（单位为s，默认30）
     */
    private int client_channel_timeout = 30;

    /**
     * 服务器服务注册管理器线程池大小
     */
    private int server_register_manager_poolSize = 2;

    private SharpRpcConfig(){}

    public static SharpRpcConfig getInstance(){
        return instance;
    }

    public SharpRpcConfig load(String propertiesFileName){
        loadProperties(propertiesFileName);
        return instance;
    }

    /**
     * 加载sharp配置文件
     */
    private void loadProperties(String propertiesFileName){
        try{
            InputStream resource = this.getClass().getClassLoader().getResourceAsStream(propertiesFileName);
            Properties pop = new Properties();
            pop.load(resource);
            loadZookeeperConfig(pop);
            loadServerConfig(pop);
            loadClientConfig(pop);
        } catch (Exception e) {
            throw new SharpConfigException("Sharp配置文件加载失败",e);
        }

    }


    /**
     * 加载zookeeper配置项
     * @param pop
     */
    private void loadZookeeperConfig(Properties pop) {

        Object zkAddress = pop.get("base.zookeeper.address");
        if(zkAddress!=null)
            this.base_zk_address = (String) zkAddress;
        else
            throw new SharpConfigException("Sharp配置文件错误: 配置项[base.zookeeper.address]不能为空");

        Object zkRetryTimes = pop.get("base.zookeeper.retryTimes");
        if(zkRetryTimes!=null){
            this.base_zk_retryTimes = Integer.parseInt((String) zkRetryTimes);
        }

        Object zkSleepBetweenRetry = pop.get("base.zookeeper.sleepBetweenRetry");
        if(zkSleepBetweenRetry!=null){
            this.base_zk_SleepBetweenRetry = Integer.parseInt((String) zkSleepBetweenRetry);
        }

        Object poolSize = pop.get("base.zookeeper.poolSize");
        if(poolSize!=null){
            this.base_zk_poolSize = Integer.parseInt((String) poolSize);
        }

    }

    /**
     * 加载服务器配置
     * @param pop
     */
    private void loadServerConfig(Properties pop) {

        Object serverEnable = pop.get("server.enable");
        if(serverEnable !=null){
            this.server_enable = Boolean.valueOf((String) serverEnable);
            // 开启rpc服务器
            if(this.server_enable){

                // 开启服务器后,服务的注册地址变为必填
                Object registerAddress = pop.get("server.service.register.address");
                if(registerAddress!=null){
                    this.server_service_register_address = (String) registerAddress;
                }else{
                    throw new SharpConfigException("Sharp配置文件错误: 配置项[server.enable]为true时,配置项[server.service.register.address]不能为空");
                }

                // 判断是否开启自动扫描服务
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

                // 设置自定义端口,否则使用默认端口
                if(pop.get("server.port")!=null){
                    this.server_port = Integer.parseInt((String) pop.get("server.port"));
                }

                // 设置eventLoopGroup初始大小
                if(pop.get("server.eventLoopGroup.size")!=null){
                    this.server_eventLoopGroup_size = Integer.parseInt((String) pop.get("server.eventLoopGroup.size"));
                }

                // 服务器收到Rpc请求后是否进行异步计算
                if(pop.get("server.compute.async")!=null){
                    this.server_compute_async = Boolean.valueOf((String) pop.get("server.compute.async"));
                }

                // 设置服务器异步处理Rpc请求的线程池大小
                if(pop.get("server.compute.poolSize")!=null){
                    this.server_compute_poolSize = Integer.parseInt((String) pop.get("server.compute.poolSize"));
                }

                // 设置服务注册管理器线程池大小
                if(pop.get("server.register.manager.poolSize")!=null){
                    this.server_register_manager_poolSize = Integer.parseInt((String) pop.get("server.register.manager.poolSize"));
                }

            }
        }

    }

    /**
     * 加载客户端配置
     * @param pop
     */
    private void loadClientConfig(Properties pop) {

        // 客户端连接池大小
        if(pop.get("client.channel.poolSize")!=null){
            this.client_channel_poolSize = Integer.parseInt((String) pop.get("client.channel.poolSize"));
        }

        // 客户端eventLoopGroup初始大小（0为不设置，采用netty默认值）
        if(pop.get("client.eventLoopGroup.size")!=null){
            this.client_eventLoopGroup_size = Integer.parseInt((String) pop.get("client.eventLoopGroup.size"));
        }

        // 客户端channel连接过期时间大小（单位为s，默认30）
        if(pop.get("client.channel.timeout")!=null){
            this.client_channel_timeout = Integer.parseInt((String) pop.get("client.channel.timeout"));
        }

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

    public int getBase_zk_SleepBetweenRetry() {
        return base_zk_SleepBetweenRetry;
    }

    public int getBase_zk_poolSize() {
        return base_zk_poolSize;
    }

    public boolean isServer_enable() {
        return server_enable;
    }

    public boolean isServer_autoScan_enable() {
        return server_autoScan_enable;
    }

    public int getServer_port() {
        return server_port;
    }

    public int getServer_eventLoopGroup_size() {
        return server_eventLoopGroup_size;
    }

    public String getServer_autoScan_base() {
        return server_autoScan_base;
    }

    public String getServer_service_register_address() {
        return server_service_register_address;
    }

    public Boolean getServer_compute_async() {
        return server_compute_async;
    }

    public int getServer_compute_poolSize() {
        return server_compute_poolSize;
    }

    public int getClient_channel_poolSize() {
        return client_channel_poolSize;
    }

    public int getClient_eventLoopGroup_size() {
        return client_eventLoopGroup_size;
    }

    public int getClient_channel_timeout() {
        return client_channel_timeout;
    }

    public int getServer_register_manager_poolSize() {
        return server_register_manager_poolSize;
    }
}
