package cn.booklish.rpc.server.model;

import java.io.Serializable;

/**
 * @Author: liuxindong
 * @Description: Rpc响应实体
 * @Create: 2017/11/21 10:17
 * @Modify:
 */
public class RpcResponse implements Serializable{

    /**
     * 消息唯一标识id
     */
    private final Integer id;

    /**
     * 消息响应结果
     */
    private final Object result;


    public RpcResponse(Integer id, Object result) {
        this.id = id;
        this.result = result;
    }

    public Integer getId() {
        return id;
    }

    public Object getResult() {
        return result;
    }
}
