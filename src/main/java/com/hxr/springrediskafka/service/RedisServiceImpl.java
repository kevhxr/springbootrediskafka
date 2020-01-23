package com.hxr.springrediskafka.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class RedisServiceImpl implements RedisService {

    @Resource(name="redisTemplate")
    RedisTemplate template;

    @Override
    public void addNewKey() {
        String key = "aa";
        if(template.hasKey(key)){
            System.out.println("get key from redis");
            System.out.println(template.opsForValue().get(key));
        }else{
            System.out.println("store key first");
            template.opsForValue().set(key,"sdfgg");
        }
    }
}
