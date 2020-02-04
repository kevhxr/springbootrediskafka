package com.hxr.springrediskafka.controller;


import com.hxr.springrediskafka.config.KafkaSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/kafka")
public class KafkaController {

    @Autowired
    KafkaSender sender;

    @ResponseBody
    @RequestMapping("/produce")
    public void sendData(){
        sender.send("heyImkevin");
    }
}
