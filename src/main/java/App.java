package cn.booklish.sharp.test;


import fun.bookish.sharp.config.ServiceExport;
import fun.bookish.sharp.config.ServiceReference;
import fun.bookish.sharp.protocol.api.ProtocolName;
import fun.bookish.sharp.protocol.config.ProtocolConfig;
import fun.bookish.sharp.registry.api.RegistryCenterType;
import fun.bookish.sharp.registry.config.RegistryConfig;
import cn.booklish.sharp.test.service.Test;
import cn.booklish.sharp.test.service.TestImpl;
import com.google.common.collect.Lists;

import java.rmi.RemoteException;
import java.util.List;

/**
 * @author Don9
 * @create 2017-12-11-13:41
 **/
public class App {

    public static void main(String[] args) throws InterruptedException, RemoteException {

        RegistryConfig registryConfig = new RegistryConfig().type(RegistryCenterType.REDIS).host("47.94.206.26").port(6380);

        //ProtocolConfig protocolConfig1 = new ProtocolConfig().name(ProtocolName.RMI).host("192.168.2.246").port(12200);

        ProtocolConfig protocolConfig2 = new ProtocolConfig().name(ProtocolName.SHARP).host("192.168.2.246").port(12211);

        List protocolList = Lists.newArrayList(protocolConfig2);

        ServiceExport<Test> serviceExport = new ServiceExport<>();

        serviceExport.setRegistry(registryConfig).setProtocols(protocolList).setInterface(Test.class).setRef(new TestImpl());

        serviceExport.export();

        Thread.sleep(5000);

        ServiceReference<Test> serviceReference = new ServiceReference<>();

        serviceReference.setRegistry(registryConfig).setInterface(Test.class);

        Test test1 = serviceReference.get();

        Test test2 = serviceReference.get();

        Test test3 = serviceReference.get();

        Test test4 = serviceReference.get();

        System.out.println(test1.run(1));

        System.out.println(test2.run(2));

        System.out.println(test3.run(3));

        System.out.println(test4.run(4));

    }

}
