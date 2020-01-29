package com.hxr.springrediskafka.service;

import com.hxr.springrediskafka.entity.RedisUser;

import java.util.Map;

public interface RedisService {

    void addNewKey();

    RedisUser login(String userName, String pwd);

    String loginValidateAfterFailed(String name);

    Map<String, Object> loginUserLock(String name);
}
