package com.hxr.springrediskafka.service.msgflow;


import com.hxr.springrediskafka.config.annotation.ConditionalOnSystemProperty;
import com.hxr.springrediskafka.util.CommonUtil;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@ConditionalOnSystemProperty(name = "mode", value = "test")
public class PureKafkaReceiver {

    @Autowired
    ConsumerFactory consumerFactory;

    ConcurrentHashMap<String, Long> offsetMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void startUp() {
        ExecutorService executorService = Executors.newFixedThreadPool(12);
        executorService.submit(this::listenFromTopics);
    }

    public void listenFromTopics() {
        Consumer consumer = consumerFactory.createConsumer();
        List<String> topics = new ArrayList<>();
        topics.add("msgtp02");
        topics.add("msgtp03");
        consumer.subscribe(topics);

        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
            for (ConsumerRecord record : records) {
                String key = record.topic() + record.partition();
                Long newOffSet = record.offset();
                //System.out.println("receive:  " + record.topic() + ",p:" + record.partition() + ",v:" + record.value() + ",o:" + newOffSet + "/" + offsetMap.get(key));
                if (offsetMap.get(key) != null && newOffSet > offsetMap.get(key)) {
                    //System.out.println("not process as " + newOffSet + "/" + offsetMap.get(key));
                    continue;
                }
                try {
                    if (!offsetMap.containsKey(key)) {
                        offsetMap.put(key, newOffSet);
                    }
                    if (CommonUtil.generateFailCase()) {
                        throw new Exception("message process failed " + record.value());
                    }

                    //System.out.println("processed:  " + record.topic() + ",p:" + record.partition() + ",v:" + record.value() + ",o:" + newOffSet + "/" + offsetMap.get(key));

                    offsetMap.put(key, newOffSet + 1);
                    consumer.commitSync();

                } catch (Exception e) {

                    //System.out.println("error:  " + record.topic() + ",p:" + record.partition() + ",v:" + record.value() + ",o:" + newOffSet + "/" + offsetMap.get(key));

                    offsetMap.put(key, newOffSet);
                    consumer.seek(new org.apache.kafka.common.TopicPartition(
                                    record.topic(),
                                    record.partition()),
                            offsetMap.get(key));
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }

                    //System.out.println("error done:  " + record.topic() + ",p:" + record.partition() + ",v:" + record.value() + ",o:" + newOffSet + "/" + offsetMap.get(key));
                }
            }

        }
    }
}
