package cn.booklish.sharp.test;


import cn.booklish.sharp.config.SharpRpcConfig;
import cn.booklish.sharp.protocol.api.ProtocolName;
import cn.booklish.sharp.registry.api.RegistryCenterType;
import cn.booklish.sharp.test.service.Test;
import cn.booklish.sharp.test.service.TestImpl;

/**
 * @author Don9
 * @create 2017-12-11-13:41
 **/
public class App {

    public static void main(String[] args) throws InterruptedException {

        SharpRpcConfig sharpRpcConfig = new SharpRpcConfig();

        sharpRpcConfig.getRegistry().type(RegistryCenterType.REDIS).host("47.94.206.26").port(6380);

        sharpRpcConfig.getProtocol().name(ProtocolName.SHARP).host("192.168.2.246").port(12200);

        sharpRpcConfig.register(Test.class,new TestImpl());

        Thread.sleep(2000);

        Test testService = sharpRpcConfig.getService(Test.class);

        System.out.println(testService.run(1));

    }

}
