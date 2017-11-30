package cn.booklish;

import cn.booklish.sharp.util.KryoUtil;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;

public class TestApp {

    public static void main(String[] args) throws Exception {
        CuratorFramework client = CuratorFrameworkFactory.newClient("47.94.206.26:2181",
                new RetryNTimes(10, 5000));
        client.start();
        byte[] bytes = client.getData().forPath("/test/TestImpl");
        System.out.println(KryoUtil.readObjectFromByteArray(bytes,String.class));
        client.close();

    }
}
