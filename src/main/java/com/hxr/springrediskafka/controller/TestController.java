package com.hxr.springrediskafka.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/test")
public class TestController {

    private static final Logger logger = LoggerFactory.getLogger(TestController.class);


    @RequestMapping("/get")
    @ResponseBody
    public String getTest() {
        String testStr = "doing test";
        logger.info("test!!!!!!!! {}", testStr);
        return testStr;
    }
}
