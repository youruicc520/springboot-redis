package com.zzt.springbootredis.seckill;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author: Administrator
 * @date: 2021/07/05
 * @description:
 */
@Component
public class RdisLuaUtil {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 使用lua脚本解决商品遗留问题,首先redis需要设置商品的库存sk:00001:qt=500
     * @param prodid
     * @param userid
     * @return
     */
    public boolean handleLua(String prodid,String userid){

        //使用lua解决商品遗留问题
        String secKillScript = "local userid=KEYS[1];\r\n" +
                "local prodid=KEYS[2];\r\n" +
                "local qtKey='sk:'..prodid..\":qt\";\r\n" +
                "local usersKey='sk:'..prodid..\":user\";\r\n" +
                "local userExists=redis.call(\"sismember\",usersKey,userid);\r\n" +
                "if tonumber(userExists)==1 then \r\n" +
                "   return 2;\r\n" +
                "end\r\n" +
                "local num= redis.call(\"get\",qtKey);\r\n" +
                "if tonumber(num)<=0 then \r\n" +
                "   return 0;\r\n" +
                "else \r\n" +
                "   redis.call(\"decr\",qtKey);\r\n" +
                "   redis.call(\"sadd\",usersKey,userid);\r\n" +
                "end\r\n" +
                "return 1";

        //加载lua脚本
        DefaultRedisScript defaultRedisScript = new DefaultRedisScript(secKillScript);
        //lua脚本默认返回的是数字类型,设置返回result为Long
        defaultRedisScript.setResultType(Long.class);
        //设置脚本中使用的参数
        List<String> keyList = new ArrayList();
        keyList.add(userid);
        keyList.add(prodid);
        //执行脚本
        Object execute = redisTemplate.execute(defaultRedisScript, keyList);

        String result = String.valueOf(execute);

        if("0".equals(result)){
            System.err.println("已抢空!!!");
        } else if("1".equals(result)){
            System.out.println("抢购成功!!!");
        } else if("2".equals(result)){
            System.err.println("该用户已经抢过!!!");
        } else {
            System.out.println("抢购异常!!!");
        }

        return true;
    }
}
