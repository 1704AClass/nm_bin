package com.ningmeng.auth;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by wangb on 2020/3/10.
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class RedisTest {
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Test
    public void testRedis(){
        //redisTemplate.boundValueOps("k1").set("v1");
        //System.out.println(redisTemplate.boundValueOps("k1").get());
        stringRedisTemplate.boundValueOps("k2").set("v2");
        System.out.println(stringRedisTemplate.boundValueOps("k2").get());
    }
}
