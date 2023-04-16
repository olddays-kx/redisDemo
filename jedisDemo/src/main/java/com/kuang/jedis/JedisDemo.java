package com.kuang.jedis;

import org.junit.Test;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Set;

/**
 * @Author: KX
 * @Description: jedis简单的操作redis五大基本数据类型
 * @DateTime: 2023/4/16
 * @Version 1.0
 */

public class JedisDemo {
    public static void main(String[] args) {
        //redis的IP地址和端口号
        Jedis jedis = new Jedis("192.168.67.131", 6379);
        //测试是否连通
        String ping = jedis.ping();
        System.out.println(ping);
    }

    /**
     * @Description:  jedis操作字符串
     */
    @Test
    public void stringDemo() {
        Jedis jedis = new Jedis("192.168.67.131", 6379);
        jedis.set("k1", "kuang");
        jedis.set("k2", "king");
        jedis.set("k3", "onePice");

        //获取所有的key
        Set<String> keys = jedis.keys("*");
        System.out.println(keys.size());

        //打印所有的value
        for (String key : keys) {
            System.out.println("该key的过期时间： " + jedis.ttl(key));
            System.out.println(key+": " + jedis.get(key));
        }
        jedis.close();
    }

    /**
     * @Description:  jedis操作list
     */
    @Test
    public void listDemo() {
        Jedis jedis = new Jedis("192.168.67.131", 6379);
        jedis.lpush("klist", "kuang", "king", "onePIce");

        List<String> klist = jedis.lrange("klist", 0, -1);
        for (String v : klist) {
            System.out.println(v);
        }
        jedis.close();
    }

    /**
     * @Description:  jedis操作set
     */
    @Test
    public void setDemo() {
        Jedis jedis = new Jedis("192.168.67.131", 6379);
        jedis.sadd("kset", "kuang", "king", "onepice");

        Set<String> kset = jedis.smembers("kset");
        System.out.println(kset);

        jedis.close();
    }

}
