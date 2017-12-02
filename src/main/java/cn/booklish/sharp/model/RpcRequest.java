package cn.booklish.sharp.model;

import java.io.Serializable;

/**
 * @author: 刘新冬(www.booklish.cn)
 * @date: 2017/12/2 15:50
 * @desc: Rpc请求实体
 */
public class RpcRequest implements Serializable{

    /**
     * 消息唯一标识id
     */
    private final Integer id;

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

    public RpcRequest(Integer id, String serviceName, String methodName, boolean async) {
        this.id = id;
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

    public Integer getId() {
        return id;
    }
}
