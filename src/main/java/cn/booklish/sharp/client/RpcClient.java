package cn.booklish.sharp.client;

import cn.booklish.sharp.client.proxy.RpcServiceProxy;
import cn.booklish.sharp.zookeeper.ZkClient;
import net.sf.cglib.proxy.Enhancer;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

/**
 * @Author: liuxindong
 * @Description: Rpc客户端
 * @Create: 2017/11/22 10:04
 * @Modify:
 */
@Component
public class RpcClient {

    private static final Logger logger = Logger.getLogger(RpcClient.class);

    private static ZkClient zkClient;

    @Autowired
    public RpcClient(ZkClient zkClient) {
        RpcClient.zkClient = zkClient;
    }

    /**
     * 获得service服务代理
     */
    public static Object getService(String serviceName, Class<?> serviceInterface){

        InetSocketAddress location = getServiceLocation(serviceName);
        if(location==null){
            return null;
        }
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(serviceInterface);
        // 回调方法
        enhancer.setCallback(new RpcServiceProxy(location, serviceName));
        // 创建代理对象
        return enhancer.create();
    }

    private static InetSocketAddress getServiceLocation(String serviceName){
        String location = zkClient.getData(serviceName, String.class);
        if(StringUtils.isNotBlank(location)){
            String[] split = location.split(":");
            return new InetSocketAddress(split[0],Integer.parseInt(split[1]));
        }
        logger.warn("未找到名称为["+serviceName+"]的Rpc服务");
        return null;
    }

}
