package cn.booklish.sharp;

import redis.clients.jedis.Jedis;

import java.util.Set;

/**
 * @author Don9
 * @create 2018-01-15-16:21
 **/
public class RedisTest {

    public static void main(String[] args) {

        Jedis jedis = new Jedis("47.94.206.26",6380);

        String key = "testService:version=1";

        jedis.sadd(key,"127.0.0.1");
        jedis.sadd(key,"127.0.0.2");
        jedis.sadd(key,"127.0.0.3");
        jedis.sadd(key,"127.0.0.4");


        jedis.srem(key,"127.0.0.1");

        Set<String> smembers = jedis.smembers(key);

        smembers.forEach(s -> System.out.println(s));


    }


}
