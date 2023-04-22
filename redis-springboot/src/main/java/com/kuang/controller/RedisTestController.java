package com.kuang.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/testRedis")
public class RedisTestController {

    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("/getString")
    public String testRedis() {
        redisTemplate.opsForValue().set("k1", "kuang");

        String k1 = (String)redisTemplate.opsForValue().get("k1");
        Integer o = (Integer) redisTemplate.opsForValue().get("kc:1001:qt");

        return k1;
    }
}
