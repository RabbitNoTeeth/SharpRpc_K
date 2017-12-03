package cn.booklish.sharp.client;

import cn.booklish.sharp.client.proxy.ProxyServiceInterceptor;
import cn.booklish.sharp.zookeeper.ZkClient;
import net.sf.cglib.proxy.Enhancer;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.net.InetSocketAddress;

/**
 * @author: 刘新冬(www.booklish.cn)
 * @date: 2017/12/2 15:42
 * @desc: Rpc客户端
 */
public class SharpClient {

    private static final Logger logger = Logger.getLogger(SharpClient.class);

    private final ZkClient zkClient;

    public SharpClient(ZkClient zkClient) {
        this.zkClient = zkClient;
    }

    /**
     * 获得service服务代理
     */
    public Object getService(String path, Class<?> serviceInterface){

        ProxyServiceInterceptor proxy = getServiceLocation(path);
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
    private ProxyServiceInterceptor getServiceLocation(String path){
        String data = zkClient.getData(path, String.class);
        // data格式为->   服务全类名;ip地址:端口
        if(StringUtils.isNotBlank(data)){
            String[] split_1 = data.split(";");
            String[] split_2 = split_1[1].split(":");
            return new ProxyServiceInterceptor(
                    new InetSocketAddress(split_2[0],Integer.parseInt(split_2[1])),split_1[0]
            );
        }
        logger.warn("[SharpRpc]: 未找到名称为["+path+"]的Rpc服务");
        return null;
    }

}
