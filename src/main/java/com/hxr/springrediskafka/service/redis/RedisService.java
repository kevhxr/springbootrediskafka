package com.hxr.springrediskafka.service.redis;

import com.hxr.springrediskafka.entity.RedisUser;
import com.hxr.springrediskafka.entity.UserBean;

import java.util.Map;

public interface RedisService {

    public static String serviceName="REDISNAME";

    void addNewKey();

    RedisUser login(String userName, String pwd);

    String loginValidateAfterFailed(String name);

    Map<String, Object> loginUserLock(String name);

    void addHashUser(UserBean userBean);

    void getHashUser(Integer id);
}
