package cn.booklish.sharp.register;

import cn.booklish.sharp.zookeeper.ZkClient;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author: 刘新冬(www.booklish.cn)
 * @date: 2017/12/2 15:52
 * @desc: Rpc注册请求消费者
 */
public class RegisterTaskConsumer implements Runnable {

    private final LinkedBlockingQueue<RegisterBean> queue;

    private final ZkClient zkClient;

    public RegisterTaskConsumer(LinkedBlockingQueue<RegisterBean> queue, ZkClient zkClient) {
        this.queue = queue;
        this.zkClient = zkClient;
    }


    @Override
    public void run() {

        while (true) {
            try {
                RegisterBean entry = queue.take();
                zkClient.createPath(entry.getPath(),entry.getValue());
            } catch(InterruptedException e){
                break;
            }
        }
        Thread.currentThread().interrupt();
    }

}
