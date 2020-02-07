package com.hxr.springrediskafka.controller;

import com.hxr.springrediskafka.config.annotation.ConditionalOnSystemProperty;
import com.hxr.springrediskafka.entity.RedisUser;
import com.hxr.springrediskafka.service.redis.RedisServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping("/redis")
@ConditionalOnSystemProperty(name = "mode", value = "Prod")
public class RedisController {

    private static final Logger logger = LoggerFactory.getLogger(DAOController.class);

    @Autowired
    RedisServiceImpl redisService;


    @RequestMapping(value = "/add")
    public void addToRedis() {
        redisService.addNewKey();
    }

    @CrossOrigin
    @ResponseBody
    @RequestMapping(value = "/login")
    public String logIn(@RequestParam(value = "username") final String userName,
                        @RequestParam(value = "pwd", required = false) final String pwd,
                        @RequestParam(value = "verCode", required = false) final String verCode) {
        System.out.println("start login");
        Map<String, Object> map = redisService.loginUserLock(userName);
        if ((Boolean) map.get("flag") == true) {
            return "login failed, user been locked for retry over limit, left:"
                    + map.get("lockTime");
        } else {
            RedisUser user = redisService.login(userName, pwd);
            if (user != null) {
                return "login success";
            } else {
                String result = redisService.loginValidateAfterFailed(userName);
                return result;
            }

        }
    }
}
