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
import javax.annotation.PreDestroy;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@ConditionalOnSystemProperty(name = "mode", value = "test")
public class PureKafkaReceiver {

    public static final String TOPIC_SWAP = "msgtp02";
    public static final String TOPIC_CASHFLOW = "msgtp03";
    private boolean shouldStop = false;

    @Autowired
    ConsumerFactory consumerFactory;

    ConcurrentHashMap<String, Long> offsetMap = new ConcurrentHashMap<>();
    ExecutorService executorService = Executors.newFixedThreadPool(1);

    @PreDestroy
    public void destroy() {
        shouldStop = true;
        executorService.shutdown();
    }

    @PostConstruct
    public void startUp() {
        executorService.submit(this::listenFromTopics);
    }

    public void listenFromTopics() {
        Consumer consumer = consumerFactory.createConsumer();
        List<String> topics = new ArrayList<>();
        topics.add(TOPIC_SWAP);
        topics.add(TOPIC_CASHFLOW);
        consumer.subscribe(topics);

        while (shouldStop) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
            for (ConsumerRecord record : records) {
                String topic = record.topic();
                String key = topic + record.partition();
                Long newOffSet = record.offset();
                //System.out.println("receive new msg:  " + topic + ",p:" + record.partition() + ",v:" + record.value() + ",o:" + newOffSet + "/" + offsetMap.get(key));
                if (offsetMap.get(key) != null && newOffSet > offsetMap.get(key)) {
                    //System.out.println("not process due to offset diff " + newOffSet + "/" + offsetMap.get(key));
                    continue;
                }
                try {
                    if (!offsetMap.containsKey(key)) {
                        offsetMap.put(key, newOffSet);
                    }
                    switch (topic) {
                        case TOPIC_SWAP:
                            processSwapMsg(record.value());
                            if (CommonUtil.generateFailCase()) {
                                throw new Exception("swap message process failed " + record.value());
                            }
                            break;
                        case TOPIC_CASHFLOW:
                            processCashFlowMsg(record.value());
                            if (CommonUtil.generateFailCase()) {
                                throw new Exception("cashFlow message process failed " + record.value());
                            }
                            break;
                    }
                    consumer.commitSync();
                    offsetMap.put(key, newOffSet + 1);
                    //System.out.println("processed done:  " + topic + ",p:" + record.partition() + ",v:" + record.value() + ",o:" + newOffSet + "/" + offsetMap.get(key));


                } catch (Exception e) {

                    //System.out.println("has error:  " + topic + ",p:" + record.partition() + ",v:" + record.value() + ",o:" + newOffSet + "/" + offsetMap.get(key));

                    offsetMap.put(key, newOffSet);
                    consumer.seek(new org.apache.kafka.common.TopicPartition(
                                    topic,
                                    record.partition()),
                            offsetMap.get(key));
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }

                    //System.out.println("error done:  " + topic + ",p:" + record.partition() + ",v:" + record.value() + ",o:" + newOffSet + "/" + offsetMap.get(key));
                }
            }
        }
    }


    public void processSwapMsg(Object value) {
        /**
         * todo
         */
    }


    public void processCashFlowMsg(Object value) {
        /**
         * todo
         */
    }
}
