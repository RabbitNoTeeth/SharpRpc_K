package cn.booklish.sharp.register;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author: 刘新冬(www.booklish.cn)
 * @date: 2017/12/2 15:52
 * @desc: Rpc注册请求生产者
 */
public class RegisterTaskProducer implements Runnable{

    private final LinkedBlockingQueue<RegisterBean> queue;

    private final RegisterBean bean;

    public RegisterTaskProducer(LinkedBlockingQueue<RegisterBean> queue, RegisterBean bean) {
        this.queue = queue;
        this.bean = bean;
    }


    @Override
    public void run() {
        try{
            queue.put(bean);
        }catch (InterruptedException e){
            Thread.currentThread().interrupt();
        }
    }

}
