package cn.booklish.sharp.client.util;


/**
 * @author: 刘新冬(www.booklish.cn)
 * @date: 2017/12/3 23:21
 * @desc: 封装Rpc响应的结果
 */
public class ResponseCallback {

    private volatile Object result;

    public void receiveMessage(Object result) throws Exception {
        synchronized (this) {
            this.result = result;
            this.notify();
        }
    }

    public Object getResult() {
        return result;
    }

}
