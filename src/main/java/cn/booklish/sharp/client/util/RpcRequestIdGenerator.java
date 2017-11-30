package cn.booklish.sharp.client.util;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: liuxindong
 * @Description: Rpc请求的表示id生成工具
 * @Create: 2017/11/23 15:53
 * @Modify:
 */
public class RpcRequestIdGenerator {

    private static final AtomicInteger integer = new AtomicInteger();

    public static Integer getId(){
        return integer.getAndIncrement();
    }

}
