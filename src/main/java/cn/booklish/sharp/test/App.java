package cn.booklish.sharp.test;


import cn.booklish.sharp.config.ServiceExport;
import cn.booklish.sharp.config.ServiceReference;
import cn.booklish.sharp.protocol.api.ProtocolName;
import cn.booklish.sharp.protocol.config.ProtocolConfig;
import cn.booklish.sharp.registry.api.RegistryCenterType;
import cn.booklish.sharp.registry.config.RegistryConfig;
import cn.booklish.sharp.test.service.Test;
import cn.booklish.sharp.test.service.TestImpl;

import java.rmi.RemoteException;

/**
 * @author Don9
 * @create 2017-12-11-13:41
 **/
public class App {

    public static void main(String[] args) throws InterruptedException, RemoteException {

        RegistryConfig registryConfig = new RegistryConfig().type(RegistryCenterType.REDIS).host("47.94.206.26").port(6380);

        ProtocolConfig protocolConfig = new ProtocolConfig().name(ProtocolName.RMI).host("192.168.2.246").port(12200);

        ServiceExport<Test> serviceExport = new ServiceExport<>();

        serviceExport.setRegistry(registryConfig).setProtocol(protocolConfig).setInterface(Test.class).setRef(new TestImpl());

        serviceExport.export();

        Thread.sleep(3000);

        ServiceReference<Test> serviceReference = new ServiceReference<>();

        serviceReference.setRegistry(registryConfig).setInterface(Test.class);

        Test test = serviceReference.get();

        System.out.println(test.run(100));

    }

}
