package com.hxr.springrediskafka.service.msgflow;

import com.hxr.springrediskafka.config.annotation.ConditionalOnSystemProperty;
import com.hxr.springrediskafka.entity.event.MsgFlowEvent;
import com.hxr.springrediskafka.util.CommonUtil;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.DependsOn;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.ConsumerSeekAware;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;


/**
 * KafkaMessageListenerContainer #invokeBatchOnMessage
 * BatchMessagingMessageListenerAdapter #onmessage#invoke
 * MessagingMessageListenerAdapter #invokeHandler
 */

@Component
@DependsOn(value = {"eodMsgExecutor", "regularMsgExecutor"})
@ConditionalOnSystemProperty(name = "mode", value = "kafka")
public class MsgKafkaConsumer implements ConsumerSeekAware, ApplicationListener<MsgFlowEvent> {

    Logger logger = LoggerFactory.getLogger(MsgKafkaConsumer.class);
    private AtomicLong savedOffset = new AtomicLong(0);
    private AtomicBoolean eodSowSwitcher = new AtomicBoolean(false);
    private ReentrantLock marketLock = new ReentrantLock();
    private Condition conditionEOD = marketLock.newCondition();
    Queue<Integer> msgQueue = new LinkedBlockingQueue<>();
    Set<Integer> msgSet = new TreeSet<>();
    ConcurrentHashMap<String, Long> offsetMap = new ConcurrentHashMap<>();

    @Resource(name = "eodMsgExecutor")
    private ExecutorService eodMsgExecutor;
    @Resource(name = "regularMsgExecutor")
    private ExecutorService regularMsgExecutor;

    @Autowired
    MsgHandlerService msgHandlerService;

    @PostConstruct
    public void postConstruct() {
        System.out.println("tt: " + Thread.currentThread().getName());
    }

    @KafkaListener(topics = {"msgtp02", "msgtp03"})
    public void getMutipleTopic(List<ConsumerRecord<?, ?>> recordList, Acknowledgment ack, Consumer consumer) {
        for (ConsumerRecord record : recordList) {
            String key = record.topic() + record.partition();
            Long newOffSet = record.offset();
            System.out.println("receive:  "+record.topic()+",p:"+record.partition()+",v:"+record.value()+",o:"+record.offset()+"/"+newOffSet);
            //logger.info("{},p{},v{}", record.topic(), record.partition(), record.value());
            try {
                if (!offsetMap.containsKey(key)) {
                    offsetMap.put(key, newOffSet);
                }
                if (CommonUtil.generateFailCase()) {
                    throw new Exception("message process failed " + record.value());
                }

                System.out.println("processed:  "+record.topic()+",p:"+record.partition()+",v:"+record.value()+",o:"+record.offset()+"/"+newOffSet);
                //logger.info("{},p{},v{}", record.topic(), record.partition(), record.value());
                offsetMap.put(key, newOffSet + 1);
                ack.acknowledge();
            } catch (Exception e) {

                System.out.println("error:  "+record.topic()+",p:"+record.partition()+",v:"+record.value()+",o:"+record.offset()+"/"+newOffSet);
                //logger.error("error {},p{},v{}", record.topic(), record.partition(), record.value());
                offsetMap.put(key, newOffSet);
                consumer.seek(new org.apache.kafka.common.TopicPartition(
                                record.topic(),
                                record.partition()),
                        offsetMap.get(key));
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                System.out.println("error done:  "+record.topic()+",p:"+record.partition()+",v:"+record.value()+",o:"+record.offset()+"/"+newOffSet);

            }
        }
    }

/*    @KafkaListener(
            topicPartitions =
            @org.springframework.kafka.annotation.TopicPartition(
                    topic = MsgHandlerService.MSG_TOPIC, partitions = {"0"}))
    public void getMessageFromKafka01(List<ConsumerRecord<?, ?>> recordList, Consumer consumer, Acknowledgment ack) {
        System.out.println("getMessageFromKafka01: "+Thread.currentThread().getName());
        handleMsgFromKafka(1, recordList, consumer, ack);
    }

    @KafkaListener(
            topicPartitions =
            @org.springframework.kafka.annotation.TopicPartition(
                    topic = MsgHandlerService.MSG_TOPIC, partitions = {"1"}))
    public void getMessageFromKafka02(List<ConsumerRecord<?, ?>> recordList, Consumer consumer, Acknowledgment ack) {
        System.out.println("getMessageFromKafka02: "+Thread.currentThread().getName());
        handleMsgFromKafka(2, recordList, consumer, ack);
    }*/

    private void handleMsgFromKafka(int handlerNo, List<ConsumerRecord<?, ?>> recordList, Consumer consumer, Acknowledgment ack) {
        for (ConsumerRecord record : recordList) {
            try {
                if (savedOffset.get() == 0) {
                    savedOffset.set(record.offset());
                }
/*                if (CommonUtil.generateFailCase()) {
                    throw new Exception("message process failed " + record.value());
                }*/
                int value = Integer.parseInt(String.valueOf(record.value()));
                if (value >= 0 && !eodSowSwitcher.get()) {
                    logger.info("[{}] Partition {} received data {} off {}", record.topic(), record.partition(), value, record.offset());
                    processMessage(value, handlerNo);
                    ack.acknowledge();
                    savedOffset.getAndIncrement();
                } else {
                    holdEod(value, ack, handlerNo);
                }
            } catch (Exception e) {
                consumer.seek(new org.apache.kafka.common.TopicPartition(
                        MsgHandlerService.MSG_TOPIC,
                        record.partition()), savedOffset.get());
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e1) {
                    logger.error(e1.toString());
                }
                logger.error(e.toString());
            }
        }
    }

    private void holdEod(int value, Acknowledgment ack, int handlerNo) {
        eodSowSwitcher.set(true);
        processMessage(value, handlerNo);
        ack.acknowledge();
        savedOffset.getAndIncrement();
        try {
            marketLock.lock();
            while (eodSowSwitcher.get()) {
                conditionEOD.await();
            }
        } catch (InterruptedException e) {
            logger.error(e.toString());
        } finally {
            marketLock.unlock();
        }
    }

    public void processMessage(int msgVal, int handlerNo) {
        if (!eodSowSwitcher.get()) {
            if (msgVal < 0) {
                logger.info("[E-Con] EOD message reached {} ", msgVal);
                eodSowSwitcher.set(true);
                processEodMsg(msgVal, handlerNo);
            } else {
                logger.info("[R-Con] Regular message reached {} by {}", msgVal, handlerNo);
                processRegularMsg(msgVal, handlerNo);
            }
        }
    }

    private void processEodMsg(int msgType, int handlerNo) {
        if (!eodMsgExecutor.isShutdown()) {
            logger.info("EOD reach, stop receiver");
            eodMsgExecutor.submit(() -> {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                msgQueue.add(msgType);
                msgSet.add(msgType);
                logger.info("[E-Con] finish process EOD Msg: {} by {}", msgType, handlerNo);
            });
        }
    }

    private void processRegularMsg(int msgType, int handlerNo) {
        if (!regularMsgExecutor.isShutdown()) {
            regularMsgExecutor.submit(() -> {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                msgQueue.add(msgType);
                msgSet.add(msgType);
                logger.info("[R-Con] finish process Regular Msg: {} by {}", msgType, handlerNo);
            });
        }
    }

    @Override
    public void onApplicationEvent(MsgFlowEvent msgFlowEvent) {
        System.out.println("onApplicationEvent: " + Thread.currentThread().getName());
        String eventType = msgFlowEvent.getSource().toString();
        switch (eventType) {
            case MsgFlowEvent.SOW_EVENT:
                wakeBySow();
                break;
            case MsgFlowEvent.EOD_EVENT:
                eodReached();
                break;
            case MsgFlowEvent.SHUTDOWN_EVENT:
                doShutDown();
                break;
        }
    }

    private void wakeBySow() {
        try {
            marketLock.lock();
            logger.info("SOW reach, recover receiver");
            eodSowSwitcher.set(false);
            conditionEOD.signalAll();
        } finally {
            marketLock.unlock();
        }
    }

    private void eodReached() {
        logger.info("EOD reach, stop receiver");
        eodSowSwitcher.set(true);
    }

    private void doShutDown() {
        logger.info("Shutdown command reach, put an end to the whole flow");
        msgHandlerService.doShutDown();
        this.eodMsgExecutor.shutdown();
        this.regularMsgExecutor.shutdown();
        msgQueue.forEach(q -> System.out.print(q + ","));
        msgSet.forEach(q -> System.out.print(q + ","));
    }
}
