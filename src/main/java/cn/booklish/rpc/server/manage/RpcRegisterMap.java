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

    private static final Map<Class<?>,Class<?>> registerMap = new ConcurrentHashMap<>();

    public static void register(Class<?> interface_type,Class<?> impl_type){
        registerMap.put(interface_type,impl_type);
    }

    public static void remove(Class<?> interface_type){
        registerMap.remove(interface_type);
    }

    public static Class<?> search(Class<?> interface_type){
        return registerMap.get(interface_type);
    }

}
