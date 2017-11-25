package cn.booklish;

import cn.booklish.rpc.server.RpcServerBootStrap;
import cn.booklish.test.TestImpl;

/**
 * Hello world!
 *
 */
public class App {

    public static void main( String[] args ) throws InterruptedException {
        new RpcServerBootStrap(9090)
                .register("testService", TestImpl.class)
                .start();
    }

}
