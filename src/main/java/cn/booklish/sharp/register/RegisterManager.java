package cn.booklish.sharp.register;

import cn.booklish.sharp.zookeeper.ZkClient;

import java.util.concurrent.*;

/**
 * @author: 刘新冬(www.booklish.cn)
 * @date: 2017/12/2 15:52
 * @desc: Rpc注册请求管理器
 */
public class RegisterManager {

    private final ZkClient zkClient;

    private static final LinkedBlockingQueue<RegisterBean> queue = new LinkedBlockingQueue<>();

    /**
     * 线程池
     */
    private static final ExecutorService exec = Executors.newFixedThreadPool(2);

    public RegisterManager(ZkClient zkClient) {
        this.zkClient = zkClient;
    }

    /**
     * 启动消费者线程,等待并将服务注册到zookeeper
     */
    public void start(){
        exec.execute(new RegisterTaskConsumer(queue, zkClient));
    }

    /**
     * 提交服务注册任务到管理器
     * @param entry
     */
    public static void submit(RegisterBean entry){

        exec.execute(new RegisterTaskProducer(queue,entry));

    }

    public void shutdown(){
        exec.shutdown();
    }


}
