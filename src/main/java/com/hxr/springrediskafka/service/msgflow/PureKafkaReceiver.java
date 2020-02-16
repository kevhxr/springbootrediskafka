package com.hxr.springrediskafka.service.msgflow;


import com.hxr.springrediskafka.config.annotation.ConditionalOnSystemProperty;
import com.hxr.springrediskafka.util.CommonUtil;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
@ConditionalOnSystemProperty(name = "mode", value = "kafka")
public class PureKafkaReceiver {

    Logger logger = LoggerFactory.getLogger(PureKafkaReceiver.class);
    public static final String TOPIC_SWAP = "msgtp02";
    public static final String TOPIC_CASHFLOW = "msgtp03";
    private boolean shouldStop = false;

    @Autowired
    ConsumerFactory consumerFactory;

    ConcurrentHashMap<String, Long> offsetMap = new ConcurrentHashMap<>();
    ExecutorService executorService = Executors.newFixedThreadPool(1);
    ExecutorService swapExecutor = Executors.newFixedThreadPool(10);
    ExecutorService cashflowExecutor = Executors.newFixedThreadPool(10);

    @PreDestroy
    public void destroy() {
        shouldStop = true;
        executorService.shutdown();
    }

    @PostConstruct
    public void startUp() {
        executorService.submit(this::listenFromTopicsVersion2);
    }

    public void listenFromTopics() {
/*        Consumer consumer = consumerFactory.createConsumer();
        List<String> topics = new ArrayList<>();
        topics.add(TOPIC_SWAP);
        topics.add(TOPIC_CASHFLOW);
        consumer.subscribe(topics);

        while (!shouldStop) {
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
                            processSwapMsg(record);
                            break;
                        case TOPIC_CASHFLOW:
                            processCashFlowMsg(record);
                            break;
                    }
                    consumer.commitSync();
                    offsetMap.put(key, newOffSet + 1);
                    System.out.println("processed done:  " + topic + ",p:" + record.partition() + ",v:" + record.value() + ",o:" + newOffSet + "/" + offsetMap.get(key));


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

                    System.out.println("error done:  " + topic + ",p:" + record.partition() + ",v:" + record.value() + ",o:" + newOffSet + "/" + offsetMap.get(key));
                }
            }
        }*/
    }

    public void listenFromTopicsVersion2() {
        Consumer consumer = consumerFactory.createConsumer();
        List<String> topics = new ArrayList<>();
        topics.add(TOPIC_SWAP);
        topics.add(TOPIC_CASHFLOW);
        consumer.subscribe(topics);

        while (!shouldStop) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
            for (ConsumerRecord record : records) {
                String topic = record.topic();
                String key = topic + record.partition();
                Long newOffSet = record.offset();
                //System.out.println("receive new msg:  " + topic + ",p:" + record.partition() + ",v:" + record.value() + ",o:" + newOffSet + "/" + offsetMap.get(key));
                if (offsetMap.get(key) != null && newOffSet > offsetMap.get(key)) {
                    System.out.println("not process due to offset diff " + newOffSet + "/" + offsetMap.get(key));
                    continue;
                }
                if (!offsetMap.containsKey(key)) {
                    offsetMap.put(key, newOffSet);
                }
                switch (topic) {
                    case TOPIC_SWAP:
                        processSwapMsg(record);
                        break;
                    case TOPIC_CASHFLOW:
                        processCashFlowMsg(record);
                        break;
                }
                consumer.commitSync();
                offsetMap.put(key, newOffSet + 1);

            }
        }
    }


    public void processSwapMsg(ConsumerRecord record) {
        swapExecutor.submit(() -> {
            boolean success = false;
            for (int i = 0; i < 3; i++) {
                success = doProcess(record, 2, TOPIC_SWAP);
                if(success){
                    break;
                }
            }
            if(!success){
                /**
                 * 如果重试失败
                 * 存磁盘？存offset？
                 * solution:
                 * 1.是把这个失败的msg先ack掉然后kafkaqueue里继续收下一条？
                 *  --
                 *  --  return()
                 * 2.是不断重试到成功为止？
                 *  --
                 *  --  while (!doProcess(record, 2, TOPIC_SWAP))
                 * 3.是直接停掉整个service？
                 *  --
                 *  --  shouldStop=true;
                 *      executors.shutdown();
                 */
            }
        });
    }


    public void processCashFlowMsg(ConsumerRecord record) {
        cashflowExecutor.submit(() -> {
            boolean success = false;
            for (int i = 0; i < 3; i++) {
                success = doProcess(record, 4, TOPIC_CASHFLOW);
                if(success){
                    break;
                }
            }
            if(!success){

            }
        });
    }

    public boolean doProcess(ConsumerRecord record, int time, String type) {
        String topic = record.topic();
        String key = topic + record.partition();
        Long newOffSet = record.offset();
        Random random = new Random();
        try {
            if (CommonUtil.generateFailCase()) {
                throw new Exception();
            }
            TimeUnit.SECONDS.sleep(random.nextInt(time));
            logger.info(record.value() + " processed done:  " + topic + ",p:" + record.partition() + ",o:" + newOffSet + "/" + offsetMap.get(key));
            return true;
        } catch (Exception e) {
            System.out.println(type + "has error:  " + topic + ",p:" + record.partition() + ",v:" + record.value() + ",o:" + newOffSet + "/" + offsetMap.get(key));
            return false;
        }
    }
}
