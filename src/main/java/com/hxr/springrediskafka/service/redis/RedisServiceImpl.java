package com.hxr.springrediskafka.service.redis;

import com.hxr.springrediskafka.config.annotation.ConditionalOnSystemProperty;
import com.hxr.springrediskafka.entity.RedisUser;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@ConditionalOnSystemProperty(name = "mode", value = "Prod")
public class RedisServiceImpl implements RedisService {

    @Resource(name = "redisTemplate")
    RedisTemplate template;

    @Override
    public void addNewKey() {
        String key = "aa";
        if (template.hasKey(key)) {
            System.out.println("get key from redis");
            System.out.println(template.opsForValue().get(key));
        } else {
            System.out.println("store key first");
            template.opsForValue().set(key, "sdfgg");
        }
    }

    @Override
    public RedisUser login(String userName, String pwd) {
        RedisUser user = null;
        String key = RedisUser.getKeyName(userName);
        if (template.hasKey(key)) {
            String password = String.valueOf(template.opsForValue().get(key));
            if (pwd.equals(password)) {
                user = new RedisUser();
                user.setPwd(pwd);
                user.setUserName(userName);
            }
        }
        return user;
    }

    @Override
    public String loginValidateAfterFailed(String name) {
        String key = RedisUser.getLoginCountFailedKey(name);
        //failed login limit
        int num = 5;
        //if user has login failed attempt
        if (template.hasKey(key)) {
            Integer loginFailedCount = (Integer) template.opsForValue().get(key);
            if (loginFailedCount < (num - 1)) {
                template.opsForValue().increment(key, 1);
                return name + " login failed, allow retry in " +
                        template.getExpire(key, TimeUnit.SECONDS)
                        + "second, allowed retry times left:" + (num - loginFailedCount - 1);
            } else {
                //over login failed limit
                //so lock user for login this account for 1 MINUTE
                String lockKey = RedisUser.getLoginTimeLock(name);
                template.opsForValue().set(lockKey, 1);
                template.expire(lockKey, 1, TimeUnit.MINUTES);
                return name + " login failed count exceed limit been locked 4 1 minutes";
            }
        } else {
            //cannot set expire time while set value
            //record login failed 4 the first time
            template.opsForValue().set(key, 1);
            template.expire(key, 20, TimeUnit.SECONDS);
            return name + " login failed, now allow login retry times:" + (num - 1);

        }
    }

    @Override
    public Map<String, Object> loginUserLock(String name) {
        System.out.println(serviceName);
        Map<String, Object> map = new HashMap();
        String key = RedisUser.getLoginTimeLock(name);
        if (template.hasKey(key)) {
            Long expireTime = template.getExpire(key, TimeUnit.SECONDS);
            map.put("flag", true);
            map.put("lockTime", expireTime);
        } else {
            map.put("flag", false);
        }
        return map;
    }
}
