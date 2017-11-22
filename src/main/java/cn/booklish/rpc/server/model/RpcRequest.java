package cn.booklish.rpc.server.model;

import java.io.Serializable;

/**
 * @Author: liuxindong
 * @Description: Rpc请求实体
 * @Create: 2017/11/21 10:17
 * @Modify:
 */
public class RpcRequest implements Serializable{

    /**
     * 请求的接口类型
     */
    private final String serviceName;

    /**
     * 请求的接口方法
     */
    private final String methodName;

    /**
     * 请求接口方法的参数类型
     */
    private Class<?>[] paramTypes;

    /**
     * 请求接口方法的参数
     */
    private Object[] paramValues;

    /**
     * 是否需要异步处理(对于等待时间长的计算任务推荐使用异步处理)
     */
    private final boolean async;

    public RpcRequest(String serviceName, String methodName, boolean async) {
        this.serviceName = serviceName;
        this.methodName = methodName;
        this.async = async;
    }


    public String getMethodName() {
        return methodName;
    }

    public Class<?>[] getParamTypes() {
        return paramTypes;
    }

    public void setParamTypes(Class<?>[] paramTypes) {
        this.paramTypes = paramTypes;
    }

    public Object[] getParamValues() {
        return paramValues;
    }

    public void setParamValues(Object[] paramValues) {
        this.paramValues = paramValues;
    }

    public boolean isAsync() {
        return async;
    }

    public String getServiceName() {
        return serviceName;
    }
}
