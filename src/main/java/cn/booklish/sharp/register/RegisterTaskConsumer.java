package cn.booklish.sharp.register;

import cn.booklish.sharp.zookeeper.ZkClient;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Rpc注册请求消费者
 */
public class RegisterTaskConsumer implements Runnable {

    private final LinkedBlockingQueue<RegisterEntry> queue;

    private final ZkClient zkClient;

    public RegisterTaskConsumer(LinkedBlockingQueue<RegisterEntry> queue, ZkClient zkClient) {
        this.queue = queue;
        this.zkClient = zkClient;
    }


    @Override
    public void run() {

        while (true) {
            try {
                RegisterEntry entry = queue.take();
                zkClient.createPath(entry.getKey(),entry.getValue());
            } catch(InterruptedException e){
                break;
            }
        }
        Thread.currentThread().interrupt();
    }

}
