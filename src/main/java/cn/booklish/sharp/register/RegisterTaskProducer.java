package cn.booklish.sharp.register;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Rpc注册请求生产者
 */
public class RegisterTaskProducer implements Runnable{

    private final LinkedBlockingQueue<RegisterEntry> queue;

    private final RegisterEntry entry;

    public RegisterTaskProducer(LinkedBlockingQueue<RegisterEntry> queue, RegisterEntry entry) {
        this.queue = queue;
        this.entry = entry;
    }


    @Override
    public void run() {
        try{
            queue.put(entry);
        }catch (InterruptedException e){
            Thread.currentThread().interrupt();
        }
    }

}
