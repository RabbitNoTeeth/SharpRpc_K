package cn.booklish.sharp.test;


import cn.booklish.sharp.client.SharpClient;
import cn.booklish.sharp.config.SharpRpcConfig;
import cn.booklish.sharp.test.service.Test;
import cn.booklish.sharp.test.service.TestImpl;

/**
 * @author Don9
 * @create 2017-12-11-13:41
 **/
public class App {

    public static void main(String[] args) throws InterruptedException {

        SharpRpcConfig config = new SharpRpcConfig("sharp.properties",clazz -> new TestImpl());

        config.autoConfigure();

        Thread.sleep(3000);

        Test service = (Test) SharpClient.INSTANCE.getService("/test/TestImpl", Test.class);
        System.out.println(service.run());

    }

}
