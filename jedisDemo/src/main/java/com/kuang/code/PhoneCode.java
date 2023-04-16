package com.kuang.code;

import redis.clients.jedis.Jedis;

import java.util.Random;

public class PhoneCode {
    public static void main(String[] args) {
        //发送验证码
        //sendCode("13700445780");

        //校验验证码
        verifyCode("13700445780", "611241");
    }


    //验证验证码
    public static void verifyCode(String phone, String code) {
        Jedis jedis = new Jedis("192.168.67.131", 6379);
        //存储验证码的key
        String codeKey = "verifyCode:" + phone + ":code";
        //从redis中获取code
        String redisCode = jedis.get(codeKey);
        if (redisCode == null) {
            System.out.println("该验证码已经失效");
        } else if (redisCode.equals(code)) {
            System.out.println("验证成功");
        } else {
            System.out.println("验证码验证失败");
        }
        jedis.close();
    }
    //发送验证码
    //一个手机号一天只能发送三次， 验证码过期时间是2分钟
    public static void sendCode(String phone) {
        Jedis jedis = new Jedis("192.168.67.131", 6379);

        //设置key的规则
        //手机号发送次数key
        String countKey = "verifyCode:" + phone + ":cout";

        //存储验证码的key
        String codeKey = "verifyCode:" + phone + ":code";

        String count = jedis.get(countKey);
        //检查该手机号码发送是否超过三次
        if (count == null) {
            //count为null表示第一次发送
            jedis.setex(countKey, 24*60*60,  "1");
        } else if (Integer.parseInt(count) <= 2) {
            //发送次数加1
            jedis.incr(countKey);
        } else if (Integer.parseInt(count) > 2){
            //表示该手机号已经发送了三次
            System.out.println("该手机号码已经重复发送验证码超过三次");
            jedis.close();
            return;
        }

        //将验证码存到redis中并设置过期时长为2分钟
        jedis.setex(codeKey, 120, getCode());
        jedis.close();

    }
    //生成6位验证码
    public static String getCode() {
        Random random = new Random();
        String code = "";
        for (int i = 0; i < 6; i++) {
            int rand = random.nextInt(10);
            code += rand;
        }
        return code;
    }
}
