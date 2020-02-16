package com.hxr.springrediskafka.config.kafka;


import com.hxr.springrediskafka.config.annotation.ConditionalOnSystemProperty;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.ConsumerSeekAware;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
@ConditionalOnSystemProperty(name = "mode", value = "kafka")
@ConfigurationProperties(prefix = "kafka")
public class KafkaReceiver implements ConsumerSeekAware {


    Logger logger = LoggerFactory.getLogger(KafkaReceiver.class);

    @Override
    public void registerSeekCallback(ConsumerSeekCallback callback) {

    }

    @Override
    public void onPartitionsAssigned(Map<TopicPartition, Long> assignments, ConsumerSeekCallback callback) {
        assignments.entrySet().forEach(assignment -> logger.info("onPartitionsAssigned {},{}", assignment.getKey(), assignment.getValue()));
    }

    @Override
    public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
        logger.info("onPartitionsRevoked {}", partitions.size());

    }

    @Override
    public void onIdleContainer(Map<TopicPartition, Long> assignments, ConsumerSeekCallback callback) {
        logger.info("onIdleContainer");
    }

    @Override
    public void unregisterSeekCallback() {

    }

    @KafkaListener(
            topicPartitions ={
            @org.springframework.kafka.annotation.TopicPartition(topic = "msgtp01",
                    partitions = {"0", "1"})})
    public void receivePartition01(List<String> data,
                                   @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
                                   @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                   @Header(KafkaHeaders.OFFSET) int offset,
                                   Acknowledgment ack
    ) {

        data.forEach(d -> logger.info("Partition {} received data {}", partition, d));
        ack.acknowledge();

    }


    /*    @KafkaListener(topics = MsgHandlerService.MSG_TOPIC)
    public void listen(String msgData,
                       @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
                       @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                       @Header(KafkaHeaders.OFFSET) int offset,
                       Acknowledgment ack) {
        logger.info("1demo receive : "+msgData+" "+partition+" "+topic+" "+offset);
        ack.acknowledge();
        System.out.println("im finish");
    }


    @KafkaListener(topicPartitions =
    @org.springframework.kafka.annotation.TopicPartition(
            topic = MsgHandlerService.MSG_TOPIC, partitions = {"0", "1"}))
    public void listen2(String msgData,
                       @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
                       @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                       @Header(KafkaHeaders.OFFSET) int offset,
                       Acknowledgment ack) {
        logger.info("2demo receive : "+msgData+" "+partition+" "+topic+" "+offset);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("sdasda");
       // ack.acknowledge();
    }*/
}
