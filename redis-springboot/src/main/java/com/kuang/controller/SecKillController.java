package com.kuang.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/secKill")
public class SecKillController {

    @Autowired
    private RedisTemplate redisTemplate;
    @GetMapping("/test")
    public String secKill() {
        String uid = new Random().nextInt(4000) + "";
        String prodid = "1001";
        //doSeckill(uid, prodid);
        doSecKillByscript(uid, prodid);
        return "success";
    }

    /**
     * @Description: 简单的秒杀实现
     * 但是并发会导致超卖 连接超时的问题(使用LettuceConnectionFactory已经配置连接池可以解决)
     */
    public boolean doSeckill(String uid, String prodid) {

        //判断userID 和prodID是否为空
        if (uid == null || prodid == null) {
            return false;
        }
        //拼接秒杀成功的userkey
        String userKey = "user:" + prodid + ":user";

        //拼接库存key
        String kcKey = "kc:" + prodid + ":qt";

        //添加乐观锁
        redisTemplate.watch(kcKey);

        //获取kc
        Integer kc = (Integer) redisTemplate.opsForValue().get(kcKey);
        if (kc == null) {
            System.out.println("秒杀还没开始");
            return false;
        }

        //判断用户是否重复秒杀
        if (redisTemplate.opsForSet().isMember(userKey, uid)) {
            System.out.println("已经秒杀成功了，不能重复秒杀");
            return false;
        }

        //判断商品库存是否充足 小于等于0秒杀结束
        if (kc <= 0) {
            System.out.println("秒杀已经结束了");
            return false;
        }
        //秒杀过程
        //使用事务来解决超卖的问题
        redisTemplate.multi();
        redisTemplate.opsForValue().decrement(kcKey);
        redisTemplate.opsForSet().add(userKey, uid);
        List<Object> results = redisTemplate.exec();
        if (results == null || results.size() == 0) {
            System.out.println("秒杀失败。。。。");
            return false;
        }


        //秒杀过程
        //库存减一
        //redisTemplate.opsForValue().decrement(kcKey);

        //保存秒杀成功的用户到redis set集合中
        //redisTemplate.opsForSet().add(userKey,uid);
        System.out.println("秒杀成功");
        return true;
    }

    //使用lua脚本解决库存遗留的问题
    public boolean doSecKillByscript(String uid, String prodid) {
        RedisScript redisScript = RedisScript.of(secKillScript, Long.class);
        List<String> list = new ArrayList();
        list.add(uid);
        list.add(prodid);
        Object execute = redisTemplate.execute(redisScript, list);


        String reString=String.valueOf(execute);
        if ("0".equals( reString )  ) {
            System.err.println("已抢空！！");
        }else if("1".equals( reString )  )  {
            System.out.println("抢购成功！！！！");
        }else if("2".equals( reString )  )  {
            System.err.println("该用户已抢过！！");
        }else{
            System.err.println("抢购异常！！");
        }
        return true;

    }

    //LUA脚本
    static String secKillScript ="local userid=KEYS[1];\r\n" +
            "local prodid=KEYS[2];\r\n" +
            "local qtkey='kc:'..prodid..\":qt\";\r\n" +
            "local usersKey='sk:'..prodid..\":usr\";\r\n" +
            "local userExists=redis.call(\"sismember\",usersKey,userid);\r\n" +
            "if tonumber(userExists)==1 then \r\n" +
            "   return 2;\r\n" +
            "end\r\n" +
            "local num= redis.call(\"get\" ,qtkey);\r\n" +
            "if tonumber(num)<=0 then \r\n" +
            "   return 0;\r\n" +
            "else \r\n" +
            "   redis.call(\"decr\",qtkey);\r\n" +
            "   redis.call(\"sadd\",usersKey,userid);\r\n" +
            "end\r\n" +
            "return 1" ;
}

