package com.zzt.springbootredis.controller;

import cn.hutool.core.util.RandomUtil;
import com.zzt.springbootredis.seckill.RdisLuaUtil;
import com.zzt.springbootredis.seckill.Seckill_redis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

/**
 * @author: Administrator
 * @date: 2021/07/05
 * @description:
 */@RestController
public class SecKillController {
     @Autowired
     private Seckill_redis seckill_redis;

     @Autowired
     private RdisLuaUtil rdisLuaUtil;
     @GetMapping("/SecKill")
     public void killPro(){
         String userId = RandomUtil.randomNumbers(5);
         String prodid = "00001";
         seckill_redis.secKillHandle(prodid,userId);
     }


    @GetMapping("/SecKillByLua")
     public void handleLua(){
        String userId = RandomUtil.randomNumbers(5);
        String prodid = "00001";
        rdisLuaUtil.handleLua(prodid,userId);
     }
}
