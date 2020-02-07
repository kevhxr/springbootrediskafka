package com.hxr.springrediskafka.controller;


import com.hxr.springrediskafka.config.annotation.ConditionalOnSystemProperty;
import com.hxr.springrediskafka.service.msgflow.MsgHandlerService;
import com.hxr.springrediskafka.util.KafkaSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/kafka")
@ConditionalOnSystemProperty(name = "mode", value = "test")
public class KafkaController {

    @Autowired
    KafkaSender sender;

    @ResponseBody
    @RequestMapping("/produce")
    public void sendData(){

        for (int i = 0; i <20 ; i++) {

            sender.send(""+i, MsgHandlerService.MSG_TOPIC);
        }
    }
}
