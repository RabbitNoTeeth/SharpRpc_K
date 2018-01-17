package cn.booklish.sharp.test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * @author Don9
 * @create 2018-01-17-13:58
 **/
public class Test {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        FutureTask<String> task = new FutureTask<>(() -> "i am liuxindong");

        task.run();

        System.out.println(task.get());

    }

}


