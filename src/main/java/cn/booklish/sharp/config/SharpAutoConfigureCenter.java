package cn.booklish.sharp.config;

import cn.booklish.sharp.client.SharpClient;
import cn.booklish.sharp.client.pool.ClientChannelManager;
import cn.booklish.sharp.register.RegisterManager;
import cn.booklish.sharp.register.RpcServiceAutoScanner;
import cn.booklish.sharp.server.RpcServerBootStrap;
import cn.booklish.sharp.server.manage.ServerRpcRequestManager;
import cn.booklish.sharp.server.manage.ServiceBeanFactory;
import cn.booklish.sharp.zookeeper.ZkClient;
import org.apache.log4j.Logger;

/**
 * @author: 刘新冬(www.booklish.cn)
 * @date: 2017/12/2 15:44
 * @desc: Sharp自动配置中心
 */
public class SharpAutoConfigureCenter {

    private static final Logger logger = Logger.getLogger(SharpAutoConfigureCenter.class);

    private final SharpRpcConfig config;

    private final ServiceBeanFactory serviceBeanFactory;

    /**
     * zookeeper操作工具类
     */
    private ZkClient zkClient;

    /**
     * 服务自动扫描器
     */
    private RpcServiceAutoScanner scanner;

    /**
     * 客户端
     */
    private SharpClient sharpClient;

    /**
     * 服务引导
     */
    private RpcServerBootStrap serverBootStrap;

    public SharpAutoConfigureCenter(SharpRpcConfig config, ServiceBeanFactory serviceBeanFactory) {
        this.config = config;
        this.serviceBeanFactory = serviceBeanFactory;
    }

    /**
     * 开始自动配置
     */
    public void autoConfigure() {

        configureZookeeper();

        configureClient();

        configureServer();

    }

    /**
     * 配置服务器
     */
    private void configureServer() {

        // 启动Rpc服务器功能
        if(config.isServer_enable()){

            // 启动服务注册管理器
            RegisterManager registerManager = RegisterManager.getInstance(zkClient,config.getServer_register_manager_poolSize());
            registerManager.start();
            logger.info("[SharpRpc]: RegisterManager服务注册管理器启动成功");

            // 启动服务自动扫描器
            if(config.isServer_autoScan_enable()){
                if(scanner==null){
                    scanner = new RpcServiceAutoScanner(config.getServer_autoScan_base(),
                                                        config.getServer_service_register_address(),registerManager);
                }
                logger.info("[SharpRpc]: ServiceAutoScanner服务自动扫描器配置完成");
            }

            // 启动服务器
            if(serverBootStrap==null){
                ServerRpcRequestManager serverRpcRequestManager =
                        ServerRpcRequestManager.getInstance(config.getServer_compute_poolSize(), serviceBeanFactory);
                serverBootStrap = new RpcServerBootStrap(config.getServer_port(), config.getClient_channel_timeout(),
                                                            config.getServer_compute_async(), serverRpcRequestManager);
            }
            serverBootStrap.start();
            logger.info("[SharpRpc]: RpcServerBootStrap引导配置完成");

        }
    }


    /**
     * 配置zookeeper
     */
    private void configureZookeeper() {
        // ZkClient为空时,创建默认的zookeeper客户端
        if(zkClient==null){
            zkClient = new ZkClient(config.getBase_zk_address(),config.getBase_zk_poolSize(),
                    config.getBase_zk_retryTimes(), config.getBase_zk_SleepBetweenRetry());
        }
        logger.info("[SharpRpc]: ZkClient配置完成");
    }

    /**
     * 配置客户端
     */
    private void configureClient() {

        // 创建客户端连接池管理器
        ClientChannelManager manager = ClientChannelManager.getInstance(config.getClient_channel_poolSize(),
                config.getClient_eventLoopGroup_size());

        // 创建客户端
        sharpClient = new SharpClient(zkClient, manager);

        logger.info("[SharpRpc]: RpcClient配置完成");
    }


    public ZkClient getZkClient() {
        return zkClient;
    }

    public void setZkClient(ZkClient zkClient) {
        this.zkClient = zkClient;
    }

    public RpcServiceAutoScanner getScanner() {
        return scanner;
    }

    public void setScanner(RpcServiceAutoScanner scanner) {
        this.scanner = scanner;
    }

    public RpcServerBootStrap getServerBootStrap() {
        return serverBootStrap;
    }

    public void setServerBootStrap(RpcServerBootStrap serverBootStrap) {
        this.serverBootStrap = serverBootStrap;
    }

    public SharpClient getSharpClient() {
        return sharpClient;
    }
}
