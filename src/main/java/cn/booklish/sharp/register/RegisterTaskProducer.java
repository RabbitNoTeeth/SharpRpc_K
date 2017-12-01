package cn.booklish.sharp.register;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Rpc注册请求生产者
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
