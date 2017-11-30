package cn.booklish.sharp.client.util;


/**
 * @Author: liuxindong
 * @Description:
 * @Create: 2017/11/23 16:31
 * @Modify:
 */
public class ResponseCallback {

    public volatile Object result;

    public void receiveMessage(Object result) throws Exception {
        synchronized (this) {
            this.result = result;
            this.notify();
        }
    }

}
