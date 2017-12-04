package cn.booklish.sharp.client.proxy;

import cn.booklish.sharp.client.pool.RpcClientChannelManager;
import cn.booklish.sharp.client.util.ChannelAttributeUtils;
import cn.booklish.sharp.client.util.ResponseCallback;
import cn.booklish.sharp.client.util.RpcRequestIdGenerator;
import cn.booklish.sharp.model.RpcRequest;
import cn.booklish.sharp.util.RpcMessageUtil;
import cn.booklish.sharp.zookeeper.GsonUtil;
import io.netty.channel.Channel;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;

/**
 * @author: 刘新冬(www.booklish.cn)
 * @date: 2017/12/2 15:41
 * @desc: Rpc服务代理接口回调
 */
public class ProxyServiceInterceptor implements MethodInterceptor {

    private final InetSocketAddress location;

    private final String serviceName;

    public ProxyServiceInterceptor(InetSocketAddress location, String serviceName) {
        this.location = location;
        this.serviceName = serviceName;
    }

    /**
     * 拦截对代理接口的方法调用,内部请求服务器,将服务器的计算结果返回
     * @param o
     * @param method
     * @param args
     * @param methodProxy
     * @return
     * @throws Throwable
     */
    @Override
    public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {

        Channel channel = RpcClientChannelManager.getChannel(location);
        Integer id = RpcRequestIdGenerator.getId();
        ResponseCallback callback = new ResponseCallback();
        ChannelAttributeUtils.putResponseCallback(channel,id,callback);
        synchronized (callback){
            RpcRequest rpcRequest = new RpcRequest(id, serviceName,method.getName(),false);
            rpcRequest.setParamTypes(method.getParameterTypes());
            rpcRequest.setParamValues(args);
            channel.writeAndFlush(RpcMessageUtil.objectToBytes(GsonUtil.toJson(rpcRequest))).sync();
            callback.wait();
        }
        return GsonUtil.toJson(callback.getResult());

    }
}