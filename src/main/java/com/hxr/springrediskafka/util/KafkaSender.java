package com.hxr.springrediskafka.util;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

public class KafkaSender {

    private static final String TOPIC = "tp01";

    Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    public void send(String payLoad) {
        logger.info("start send {} to topic {}", payLoad, TOPIC);
        kafkaTemplate.send(TOPIC, payLoad);
    }


    public void send(String payLoad, String topic) {
        logger.info("start send {} to topic {}", payLoad, topic);
        kafkaTemplate.send(topic, payLoad);
    }
}
