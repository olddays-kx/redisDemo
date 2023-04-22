package com.kuang.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

public class RedisClusterLock {

    @Autowired
    private RedisTemplate redisTemplate;

    public void testLock() {

        //redisTemplate.opsForValue().setIfAbsent()
    }
}
