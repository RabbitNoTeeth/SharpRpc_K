package cn.booklish.sharp.register;

import cn.booklish.sharp.zookeeper.ZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;

/**
 * Rpc注册请求管理器
 */
@Component
public class RegisterManager {

    private final ZkClient zkClient;

    private static final LinkedBlockingQueue<RegisterEntry> queue = new LinkedBlockingQueue<>();

    /**
     * 线程池
     */
    private static final ExecutorService exec = Executors.newFixedThreadPool(2);

    @Autowired
    public RegisterManager(ZkClient zkClient) {
        this.zkClient = zkClient;
        start();
    }


    private void start(){
        exec.execute(new RegisterTaskConsumer(queue, zkClient));
    }

    public static void submit(RegisterEntry entry){

        exec.execute(new RegisterTaskProducer(queue,entry));

    }


}
