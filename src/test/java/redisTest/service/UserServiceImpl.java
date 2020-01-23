package redisTest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Override
    public String getUserFromCache(String key) {
        ValueOperations<String, String> string = redisTemplate.opsForValue();
        //redisTemplate.expire("h1", 2, TimeUnit.HOURS);
        redisTemplate.opsForValue().set("h22","hhyy",2,TimeUnit.HOURS);

        if (redisTemplate.hasKey(key)) {
            System.out.println("get from Redis:");
            return string.get(key);
        } else {
            String result = "my test";
            string.set(key, result);
            System.out.println("get from DB:");
            return result;

        }
    }

}
