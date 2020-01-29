package com.hxr.springrediskafka.controller;


import com.hxr.springrediskafka.entity.UserBean;
import com.hxr.springrediskafka.service.RedisServiceImpl;
import com.hxr.springrediskafka.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;


@Controller
@RequestMapping("/test")
public class TestController {

    private static final Logger logger = LoggerFactory.getLogger(TestController.class);

    @Resource(name = "userServiceImpl")
    private UserService userService;

    @Autowired
    RedisServiceImpl redisService;

    @RequestMapping("/get")
    @ResponseBody
    public String getTest() {
        String testStr = "doing test";
        logger.info("test!!!!!!!! {}", testStr);
        return testStr;
    }


    @ResponseBody
    @RequestMapping(value = "/get/all")
    public List<UserBean> findAllUsers() {
        logger.info("findallusers==========");
        List<UserBean> users = userService.findAllUser();
        return users;
    }


}
