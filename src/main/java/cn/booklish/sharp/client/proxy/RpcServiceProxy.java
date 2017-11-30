package cn.booklish.sharp.client.proxy;

import cn.booklish.sharp.client.pool.RpcClientChannelManager;
import cn.booklish.sharp.client.util.ChannelAttributeUtils;
import cn.booklish.sharp.client.util.ResponseCallback;
import cn.booklish.sharp.client.util.RpcRequestIdGenerator;
import cn.booklish.sharp.model.RpcRequest;
import io.netty.channel.Channel;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;

/**
 * @Author: liuxindong
 * @Description: Service代理接口实现类
 * @Create: 2017/11/23 10:16
 * @Modify:
 */
public class RpcServiceProxy implements MethodInterceptor {

    private final InetSocketAddress location;

    private final String serviceName;

    public RpcServiceProxy(InetSocketAddress location, String serviceName) {
        this.location = location;
        this.serviceName = serviceName;
    }

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
            channel.writeAndFlush(rpcRequest).sync();
            callback.wait();
        }
        return callback.result;

    }
}