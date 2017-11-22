package cn.booklish.rpc.server.manage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: liuxindong
 * @Description: Rpc服务注册管理
 * @Create: 2017/11/22 9:25
 * @Modify:
 */
public class RpcRegisterMap {

    private static final Map<String,Class<?>> registerMap = new ConcurrentHashMap<>();

    public static void register(String serviceName,Class<?> impl_type){
        registerMap.put(serviceName,impl_type);
    }

    public static void remove(String serviceName){
        registerMap.remove(serviceName);
    }

    public static Class<?> search(String serviceName){
        return registerMap.get(serviceName);
    }

}
