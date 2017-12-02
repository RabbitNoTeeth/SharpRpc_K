package cn.booklish.sharp.model;

import java.io.Serializable;

/**
 * @author: 刘新冬(www.booklish.cn)
 * @date: 2017/12/2 15:51
 * @desc: Rpc响应实体
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

    /**
     * 是否成功
     */
    private final boolean success;

    /**
     * 异常信息
     */
    private Exception e;




    public RpcResponse(Integer id, Object result, boolean success) {
        this.id = id;
        this.result = result;
        this.success = success;
    }

    public RpcResponse(Integer id, boolean success, Exception e) {
        this.id = id;
        this.result = null;
        this.success = success;
        this.e = e;
    }

    public Integer getId() {
        return id;
    }

    public Object getResult() {
        return result;
    }

    public boolean isSuccess() {
        return success;
    }

    public Exception getE() {
        return e;
    }

    public void setE(Exception e) {
        this.e = e;
    }
}
