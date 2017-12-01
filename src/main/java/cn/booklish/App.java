package cn.booklish;

import cn.booklish.sharp.client.RpcClient;
import cn.booklish.test.TestInterface;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Hello world!
 *
 */
@Configuration
@ComponentScan
public class App {

    public static void main( String[] args ) throws InterruptedException {

        ApplicationContext context =
                new AnnotationConfigApplicationContext(App.class);


    }

}
