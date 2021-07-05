package com.zzt.springbootredis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: Administrator
 * @date: 2021/07/02
 * @description:
 */
@RestController
public class RedisController {

    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("/setValue")
    public String operateRedis(){
        redisTemplate.opsForValue().set("bootRedis","test");
        String bootRedis = (String)redisTemplate.opsForValue().get("bootRedis");
        return bootRedis;
    }
}
