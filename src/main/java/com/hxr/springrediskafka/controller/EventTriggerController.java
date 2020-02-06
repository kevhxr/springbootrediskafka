package com.hxr.springrediskafka.controller;

import com.hxr.springrediskafka.service.observe.DBEventListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/event")
public class EventTriggerController {

    @Autowired
    DBEventListener eventListener;

    @ResponseBody
    @RequestMapping("/sow")
    public void triggerSow(){
        eventListener.sendSowEvent();
    }


    @ResponseBody
    @RequestMapping("/shut")
    public void triggerShutDown(){
        eventListener.sendShutDownEvent();
    }


    @ResponseBody
    @RequestMapping("/eod")
    public void triggerEod(){
        eventListener.sendEodvent();
    }
}
