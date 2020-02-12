package com.hxr.springrediskafka.service.msgflow;

import com.alibaba.fastjson.JSON;
import com.hxr.springrediskafka.config.annotation.ConditionalOnSystemProperty;
import com.hxr.springrediskafka.entity.kafka.TradeMsg;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@ConditionalOnSystemProperty(name = "mode", value = "prod")
public class MsgKafkaJsonConsumer {


    Logger logger = LoggerFactory.getLogger(MsgKafkaJsonConsumer.class);
    public final static String TRADE_TOPIC = "msgtp03";

    @KafkaListener(
            topicPartitions =
            @org.springframework.kafka.annotation.TopicPartition(
                    topic = TRADE_TOPIC, partitions = {"0"}))
    public void getMessageFromKafka01(List<ConsumerRecord<Integer, String>> recordList, Consumer consumer, Acknowledgment ack) {

        processMessage(1, recordList, ack);
    }


    @KafkaListener(
            topicPartitions =
            @org.springframework.kafka.annotation.TopicPartition(
                    topic = TRADE_TOPIC, partitions = {"1"}))
    public void getMessageFromKafka02(List<ConsumerRecord<Integer, String>> recordList, Consumer consumer, Acknowledgment ack) {
        processMessage(2, recordList, ack);
    }

    private void processMessage(int handlerNo, List<ConsumerRecord<Integer, String>> recordList, Acknowledgment ack) {
        for (ConsumerRecord<Integer, String> record : recordList) {
            try {
                String value = record.value();
                Integer key = record.key();
                TradeMsg tradeMsg = JSON.parseObject(value, TradeMsg.class);
                logger.info("[{}]:{} -- {}", key, handlerNo, tradeMsg);
                ack.acknowledge();
            } catch (Exception e) {
                logger.error(e.toString());
            }
        }
    }

}
