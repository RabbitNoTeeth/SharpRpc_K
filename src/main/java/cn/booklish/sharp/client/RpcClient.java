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
public class RpcClient {

    private static final Logger logger = Logger.getLogger(RpcClient.class);

    private static ZkClient zkClient;

    public RpcClient(ZkClient zkClient) {
        RpcClient.zkClient = zkClient;
    }

    /**
     * 获得service服务代理
     */
    public static Object getService(String path, Class<?> serviceInterface){

        RpcServiceProxy proxy = getServiceLocation(path);
        if(proxy==null){
            return null;
        }
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(serviceInterface);
        // 回调方法
        enhancer.setCallback(proxy);
        // 创建代理对象
        return enhancer.create();

    }

    /**
     * 获取服务地址并创建服务代理的回调
     * @param path
     * @return
     */
    private static RpcServiceProxy getServiceLocation(String path){
        String data = zkClient.getData(path, String.class);
        // data格式为->   服务全类名;ip地址:端口
        if(StringUtils.isNotBlank(data)){
            String[] split_1 = data.split(";");
            String[] split_2 = split_1[1].split(":");
            return new RpcServiceProxy(
                    new InetSocketAddress(split_2[0],Integer.parseInt(split_2[1])),split_1[0]
            );
        }
        logger.warn("未找到名称为["+path+"]的Rpc服务");
        return null;
    }

}
