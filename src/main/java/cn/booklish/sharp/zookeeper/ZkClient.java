package cn.booklish.sharp.zookeeper;


import cn.booklish.sharp.exception.zookeeper.*;
import cn.booklish.sharp.util.KryoSerializerUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.retry.RetryNTimes;
import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;

import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;

/**
 * @author: 刘新冬(www.booklish.cn)
 * @date: 2017/12/2 16:14
 * @desc: zookeeper工具类
 */
public class ZkClient {

    private static final Logger logger = Logger.getLogger(ZkClient.class);

    private final String zkAddress;

    private int connectionPoolSize = 15;

    private int zkRetryTimes = 10;

    private int zkSleepBetweenRetry = 5000;

    private final ZkConnectionPool pool;

    public ZkClient(String zkAddress){
        this.zkAddress = zkAddress;
        this.pool = new ZkConnectionPool(zkAddress,connectionPoolSize,zkRetryTimes,zkSleepBetweenRetry);
    }

    public ZkClient(String zkAddress,int connectionPoolSize){
        this.zkAddress = zkAddress;
        this.connectionPoolSize = connectionPoolSize;
        this.pool = new ZkConnectionPool(zkAddress,connectionPoolSize,zkRetryTimes,zkSleepBetweenRetry);
    }

    public ZkClient(String zkAddress, int zkRetryTimes, int zkSleepBetweenRetry){

        this.zkAddress = zkAddress;
        this.zkRetryTimes = zkRetryTimes;
        this.zkSleepBetweenRetry = zkSleepBetweenRetry;
        this.pool = new ZkConnectionPool(zkAddress,connectionPoolSize,zkRetryTimes,zkSleepBetweenRetry);

    }

    public ZkClient(String zkAddress, int connectionPoolSize, int zkRetryTimes, int zkSleepBetweenRetry){

        this.zkAddress = zkAddress;
        this.connectionPoolSize = connectionPoolSize;
        this.zkRetryTimes = zkRetryTimes;
        this.zkSleepBetweenRetry = zkSleepBetweenRetry;
        this.pool = new ZkConnectionPool(zkAddress,connectionPoolSize,zkRetryTimes,zkSleepBetweenRetry);

    }




    /**
     * 获取指定节点的所有子节点
     * @param path
     * @param watcher
     * @return
     */
    public List<String> getChildrenPath(String path, CuratorWatcher watcher){
        try {
            return pool.getConnection().getChildren().usingWatcher(watcher).forPath(path);
        } catch (Exception e) {
            logger.warn("获取zookeeper子节点列表失败");
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取指定节点的数据
     * @param path
     * @return
     */
    public byte[] getData(String path){
        try {
            return pool.getConnection().getData().forPath(path);
        } catch (Exception e) {
            logger.error("获取zookeeper路径["+path+"]数据失败");
            throw new GetZkPathDataException("获取zookeeper路径["+path+"]数据失败",e);
        }
    }

    /**
     * 获取指定节点的数据
     * @param path
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T getData(String path,Class<T> clazz){
        try {
            return KryoSerializerUtil.readObjectFromByteArray(pool.getConnection().getData().forPath(path),clazz);
        } catch (Exception e) {
            logger.error("获取zookeeper路径["+path+"]数据失败");
            throw new GetZkPathDataException("获取zookeeper路径["+path+"]数据失败",e);
        }
    }

    /**
     * 创建节点
     * @param path
     * @param data
     */
    public void createPath(String path, Object data){

        try {
            if(!checkPathExists(path)){
                checkParentExits(path);
                pool.getConnection().create().withMode(CreateMode.PERSISTENT).withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                        .forPath(path, KryoSerializerUtil.writeObjectToByteArray(data));
            }
            else{
                updatePath(path,data);
            }
        } catch (Exception e) {
            logger.error("创建zookeeper路径["+path+"]失败");
            throw new CreateZkPathException("创建zookeeper路径["+path+"]失败",e);
        }

    }

    private void checkParentExits(String path){
        try{
            checkAndCreateParent(path.substring(0,path.lastIndexOf("/")));
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    private void checkAndCreateParent(String path){

        if(StringUtils.isNotBlank(path)){
            if(!checkPathExists(path)){
                String parent = path.substring(0, path.lastIndexOf("/"));
                if(StringUtils.isNotBlank(parent)){
                    checkAndCreateParent(parent);
                }
                createPath(path,"");
            }
        }

    }


    /**
     * 更新节点数据
     * @param path
     * @param data
     */
    public void updatePath(String path, Object data){

        try {

            if(checkPathExists(path))
                pool.getConnection().setData().forPath(path, KryoSerializerUtil.writeObjectToByteArray(data));

        } catch (Exception e) {
            logger.error("更新指定的zookeeper路径["+path+"]失败");
            throw new UpdateZkPathException("更新指定的zookeeper路径["+path+"]失败",e);
        }

    }

    /**
     * 删除节点
     * @param path
     */
    public void deletePath(String path){

        try {

            if(checkPathExists(path))
                pool.getConnection().delete().withVersion(-1).forPath(path);

        } catch (Exception e) {
            logger.error("删除指定的zookeeper路径["+path+"]失败");
            throw new DeleteZkPathException("删除指定的zookeeper路径["+path+"]失败",e);
        }

    }

    /**
     * 检查节点是否存在
     * @param path
     * @return
     */
    public boolean checkPathExists(String path){

        try {
            return pool.getConnection().checkExists().forPath(path) != null;
        } catch (Exception e) {
            logger.error("检查指定的zookeeper路径["+path+"]是否存在失败");
            throw new CheckZkPathExistsException("检查指定的zookeeper路径["+path+"]是否存在失败");
        }

    }

    private class ZkConnectionPool{

        private final String zkAddress;

        private final int poolSize;

        private final int zkRetryTimes;

        private final int zkSleepBetweenRetry;

        private final CuratorFramework[] pool;

        private final Object[] locks;

        public ZkConnectionPool(String zkAddress, int poolSize, int zkRetryTimes, int zkSleepBetweenRetry){
            this.zkAddress = zkAddress;
            this.poolSize = poolSize;
            this.zkRetryTimes = zkRetryTimes;
            this.zkSleepBetweenRetry = zkSleepBetweenRetry;
            this.pool = new CuratorFramework[poolSize];
            this.locks = new Object[poolSize];
            for(int x=0;x<poolSize;x++){
                locks[x] = new Object();
            }
        }

        public CuratorFramework getConnection(){
            int index = new Random().nextInt(poolSize);
            CuratorFramework connection = pool[index];
            if(connection!=null && connection.getState().equals(CuratorFrameworkState.STARTED)){
                return connection;
            }
            synchronized (locks[index]){
                connection = pool[index];
                if(connection!=null && connection.getState().equals(CuratorFrameworkState.STARTED)){
                    return connection;
                }
                CuratorFramework newConnection = createConnection(zkAddress, zkRetryTimes, zkSleepBetweenRetry);
                pool[index] = newConnection;
                return newConnection;
            }
        }

        private CuratorFramework createConnection(String zkAddress, int zkRetryTimes, int zkSleepBetweenRetry) {
            //设置信号量,最多允许重试5次
            Semaphore semaphore = new Semaphore(3);
            do {
                try{
                    if(semaphore.tryAcquire()){
                        CuratorFramework connection = CuratorFrameworkFactory.newClient(zkAddress,
                                new RetryNTimes(zkRetryTimes, zkSleepBetweenRetry));
                        connection.start();
                        return connection;
                    }else{
                        return null;
                    }
                }catch (Exception e){
                    //重试
                    logger.info("[SharpRpc]: 获取zookeeper连接失败,重新尝试连接...");
                }
            }while (true);
        }

    }


}
