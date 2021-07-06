package com.zzt.springbootredis.seckill;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * @author: Administrator
 * @date: 2021/07/05
 * @description:
 */
@Component
public class Seckill_redis {
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 使用watch监听,并把key组队multi(),并且提交事务exec()
     * @param prodid
     * @param userid
     * @return
     */
    public boolean secKillHandle(String prodid,String userid){

        //1.判断商品id和用户id是否为空,为空则返回false
        if(prodid == null || userid == null){
            return false;
        }
        //2.定义秒杀商品的key以及用户id的key
        String kcKey = "sk:"+prodid+":qt";
        String userKey = "sk:"+prodid+":user";

        //开启redis事务
        redisTemplate.setEnableTransactionSupport(true);

        //使用监听
        redisTemplate.watch(kcKey);

        //获取库存数量,如果为null说明秒杀还没有开始
        String kcNum = String.valueOf(redisTemplate.opsForValue().get(kcKey));

        if(kcKey==null){
            System.out.println("秒杀还没有开始...");
            return false;
        }


        //判断用户是否重复秒杀
        Set members = redisTemplate.opsForSet().members(userKey);
        if(!members.isEmpty() ){
            //如果userid存在,则不能进行秒杀
            if(members.contains(userid)) {
                System.out.println("已经秒杀成功,不能重复秒杀.....");
                return false;
            }
        }


        //3.判断库存是否大于0,大于则库存减1,否则秒杀是吧

        if(Integer.parseInt(kcNum)<=0){
            System.out.println("秒杀已经结束了");
            return false;
        }

        //使用事务处理库存和用户id
        redisTemplate.multi();

        //4.秒杀成功,库存减1,增加用户id
        redisTemplate.opsForValue().decrement(kcKey);
        redisTemplate.opsForSet().add(userKey,userid);

        //提交事务
        List exec = redisTemplate.exec();
        if(exec==null || exec.size()==0){
            //System.out.println("事务执行失败");
            return false;
        }

        System.out.println("秒杀成功了....");

        return true;
    }
}
