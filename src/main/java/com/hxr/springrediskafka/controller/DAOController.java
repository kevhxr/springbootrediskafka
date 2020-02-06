package com.hxr.springrediskafka.controller;


import com.hxr.springrediskafka.entity.UserBean;
import com.hxr.springrediskafka.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;


@Controller
@RequestMapping("/test")
public class DAOController {

    private static final Logger logger = LoggerFactory.getLogger(DAOController.class);

    @Resource(name = "userServiceImpl")
    private UserService userService;

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

    @ResponseBody
    @RequestMapping(value = "/add/{userName}/{userAlias}/{userAge}")
    public int insertUser(
            @PathVariable("userName") String userName,
            @PathVariable("userAlias") String userAlias,
            @PathVariable("userAge") int userAge) throws Exception {
        logger.info("going to insert User==========");

        UserBean userBean = new UserBean();
        userBean.setUserName(userName);
        userBean.setUserAge(userAge);
        userBean.setUserAlias(userAlias);
        int insertRowNum = userService.insertUser(userBean);
        logger.info("successfully insert User==========" + userBean.toString());
        return insertRowNum;
    }


}
