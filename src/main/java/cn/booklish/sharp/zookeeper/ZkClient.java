package cn.booklish.sharp.zookeeper;


import cn.booklish.sharp.constant.RpcConfigInfo;
import cn.booklish.sharp.exception.*;
import cn.booklish.sharp.util.KryoUtil;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.retry.RetryNTimes;
import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ZkClient {

    private static final Logger logger = Logger.getLogger(ZkClient.class);


    private final RpcConfigInfo rpcConfigInfo;

    final CuratorFramework client;

    @Autowired
    public ZkClient(RpcConfigInfo rpcConfigInfo){
        this.rpcConfigInfo = rpcConfigInfo;
        client = CuratorFrameworkFactory.newClient(rpcConfigInfo.base_zk_address,
                new RetryNTimes(rpcConfigInfo.base_zk_retryTimes, rpcConfigInfo.base_zk_SleepBetweenRetry));
        client.start();
    }


    /**
     * 获取指定节点的所有子节点
     * @param path
     * @param watcher
     * @return
     */
    public List<String> getChildrenPath(String path, CuratorWatcher watcher){
        try {
            return client.getChildren().usingWatcher(watcher).forPath(path);
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
            return client.getData().forPath(path);
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
            return KryoUtil.readObjectFromByteArray(client.getData().forPath(path),clazz);
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
            if(!checkPathExists(path))
                client.create().withMode(CreateMode.PERSISTENT).withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                        .forPath(path,KryoUtil.writeObjectToByteArray(data));
            else
                updatePath(path,data);

        } catch (Exception e) {
            logger.error("创建zookeeper路径["+path+"]失败");
            throw new CreateZkPathException("创建zookeeper路径["+path+"]失败",e);
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
                client.setData().forPath(path,KryoUtil.writeToByteArray(data));

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
                client.delete().withVersion(-1).forPath(path);

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
            if(client.checkExists().forPath(path)!=null)
                return true;
            else
                return false;
        } catch (Exception e) {
            logger.error("检查指定的zookeeper路径["+path+"]是否存在失败");
            throw new CheckZkPathExistsException("检查指定的zookeeper路径["+path+"]是否存在失败");
        }

    }


}
