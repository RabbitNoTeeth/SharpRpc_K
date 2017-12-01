package cn.booklish.sharp.config;

import cn.booklish.sharp.client.RpcClient;
import cn.booklish.sharp.register.RegisterManager;
import cn.booklish.sharp.register.ServiceScanner;
import cn.booklish.sharp.server.RpcServerBootStrap;
import cn.booklish.sharp.zookeeper.ZkClient;
import org.apache.log4j.Logger;

/**
 * Sharp自动配置中心,根据 SharpRpcConfig 中的配置项自动创建服务流程
 */
public class SharpAutoConfigureCenter {

    private static final Logger logger = Logger.getLogger(SharpAutoConfigureCenter.class);

    private final SharpRpcConfig config;

    /**
     * zookeeper操作工具类
     */
    private ZkClient zkClient;

    /**
     * 服务自动扫描器
     */
    private ServiceScanner scanner;

    /**
     * 客户端
     */
    private RpcClient rpcClient;

    /**
     * 服务引导
     */
    private RpcServerBootStrap serverBootStrap;

    public SharpAutoConfigureCenter(SharpRpcConfig config) {
        this.config = config;
    }

    /**
     * 开始自动配置
     */
    public void autoConfigure() {

        // ZkClient为空时,创建默认的zookeeper客户端
        if(zkClient==null){
            zkClient = new ZkClient(config.getBase_zk_address(),config.getBase_zk_retryTimes(),
                    config.getBase_zk_SleepBetweenRetry());
        }
        logger.info("[SharpRpc]: ZkClient配置完成");

        // 创建客户端
        rpcClient = new RpcClient(zkClient);
        logger.info("[SharpRpc]: RpcClient配置完成");

        // 启动Rpc服务器功能
        if(config.isServer_enable()){

            // 启动服务注册管理器
            RegisterManager registerManager = new RegisterManager(zkClient);
            registerManager.start();
            logger.info("[SharpRpc]: RegisterManager服务注册管理器启动成功");

            // 启动服务自动扫描器
            if(config.isServer_autoScan_enable()){
                if(scanner==null){
                    scanner = new ServiceScanner(config.getServer_autoScan_base(),config.getServer_service_register_address());
                }
                logger.info("[SharpRpc]: ServiceScanner配置完成");
            }

            // 启动服务器
            if(serverBootStrap==null){
                serverBootStrap = new RpcServerBootStrap(config.getServer_port());
            }
            serverBootStrap.start();
            logger.info("[SharpRpc]: RpcServerBootStrap启动成功");


        }

    }


    public ZkClient getZkClient() {
        return zkClient;
    }

    public void setZkClient(ZkClient zkClient) {
        this.zkClient = zkClient;
    }

    public ServiceScanner getScanner() {
        return scanner;
    }

    public void setScanner(ServiceScanner scanner) {
        this.scanner = scanner;
    }

    public RpcServerBootStrap getServerBootStrap() {
        return serverBootStrap;
    }

    public void setServerBootStrap(RpcServerBootStrap serverBootStrap) {
        this.serverBootStrap = serverBootStrap;
    }
}
