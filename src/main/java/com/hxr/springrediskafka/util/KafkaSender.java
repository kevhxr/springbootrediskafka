package com.hxr.springrediskafka.util;

import com.alibaba.fastjson.JSON;
import com.hxr.springrediskafka.entity.kafka.TradeMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Random;

public class KafkaSender {

    private static final String TOPIC = "tp01";

    Logger logger = LoggerFactory.getLogger(KafkaSender.class);
/*
    @Autowired
    KafkaTemplate<Integer, String> kafkaTemplate;

    public void send(String payLoad) {
        logger.info("start send {} to topic {}", payLoad, TOPIC);
        kafkaTemplate.send(TOPIC, payLoad);
    }


    public void send(String payLoad, String topic) {
        logger.info("start send {} to topic {}", payLoad, topic);
        kafkaTemplate.send(topic, payLoad);
    }


    public void sendJson(String topic) {
        Random random = new Random();
        int randInt = random.nextInt(100);
        TradeMsg tradeMsg = new TradeMsg(randInt, "CITI", randInt * 5, randInt * 10);
        String json = JSON.toJSONString(tradeMsg);
        logger.info("start send {} to topic {}", json, topic);
        kafkaTemplate.send(topic, randInt, json);
    }*/
}
