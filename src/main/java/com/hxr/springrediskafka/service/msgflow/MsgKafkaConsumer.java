package com.hxr.springrediskafka.service.msgflow;

import com.hxr.springrediskafka.entity.MsgFlowEvent;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.DependsOn;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.ConsumerSeekAware;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

@Component
@DependsOn(value = {"eodMsgExecutor", "regularMsgExecutor"})
public class MsgKafkaConsumer implements ConsumerSeekAware, ApplicationListener<MsgFlowEvent> {

    Logger logger = LoggerFactory.getLogger(MsgKafkaConsumer.class);
    private AtomicLong savedOffset = new AtomicLong(0);
    private AtomicBoolean eodSowSwitcher = new AtomicBoolean(false);
    Object lock = new Object();
    Queue<Integer> msgQueue = new LinkedBlockingQueue<>();

    @Resource(name = "eodMsgExecutor")
    private ExecutorService eodMsgExecutor;
    @Resource(name = "regularMsgExecutor")
    private ExecutorService regularMsgExecutor;

    @Autowired
    MsgHandlerService msgHandlerService;

    @KafkaListener(
            topicPartitions =
            @org.springframework.kafka.annotation.TopicPartition(
                    topic = MsgHandlerService.MSG_TOPIC, partitions = {"0", "1"}))
    public synchronized void getMessageFromKafka(
            List<ConsumerRecord<?, ?>> recordList,
            Consumer consumer,
            Acknowledgment ack) {

        for (ConsumerRecord record : recordList) {
            try {
                if (savedOffset.get() == 0) {
                    savedOffset.set(record.offset());
                }
                int value = Integer.parseInt(String.valueOf(record.value()));
                if (value >= 0 && !eodSowSwitcher.get()) {
                    logger.info("[{}] Partition {} received data {} off {}",
                            record.topic(),
                            record.partition(), value, record.offset());
                    processMessage(value);
                    ack.acknowledge();
                    savedOffset.set(savedOffset.get() + 1);
                } else {
                    consumer.seek(new org.apache.kafka.common.TopicPartition(
                            MsgHandlerService.MSG_TOPIC,
                            record.partition()), savedOffset.get());
/*                    while (eodSowSwitcher.get()) {
                        consumer.seek(new org.apache.kafka.common.TopicPartition(
                                MsgHandlerService.MSG_TOPIC,
                                record.partition()), savedOffset.get()-1);
                        this.wait();
                    }*/
                }
            } catch (Exception e) {
                logger.error(e.toString());
            }
        }
    }

/*    @KafkaListener(topicPartitions = {
            @org.springframework.kafka.annotation.TopicPartition(
                    topic = MsgHandlerService.MSG_TOPIC, partitions = {"0", "1"})})
    public void getMessageKafka(
            List<String> dataList,
            List<ConsumerRecord<?, ?>> recordList,
            @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.OFFSET) int offset,
            Consumer consumer,
            Acknowledgment ack
    ) {
        for (String data : dataList) {
            try {
                if (savedOffset == 0) {
                    savedOffset = offset;
                }
                int value = Integer.parseInt(data);
                if (value >= 0 && !eodSowSwitcher.get()) {
                    logger.info("[{}] P: {} received data {} off {}",
                            topic, partition, value, offset);
                    processMessage(value);
                    ack.acknowledge();
                    savedOffset++;
                } else {
                    synchronized (lock) {
                        while (eodSowSwitcher.get()) {
                            consumer.seek(new org.apache.kafka.common.TopicPartition(
                                    MsgHandlerService.MSG_TOPIC,
                                    partition), savedOffset);
                            lock.wait();
                        }
                    }
                }
            } catch (Exception e) {
                logger.error(e.toString());
            }
        }
    }*/

    public void processMessage(int msgVal) {
        if (!eodSowSwitcher.get()) {
            //int msgVal = msgQueue.remove();
            if (msgVal < 0) {
                logger.info("[E-Con] EOD message reached {}", msgVal);
                eodSowSwitcher.set(true);
                processEodMsg(msgVal);
            } else {
                logger.info("[R-Con] Regular message reached {}", msgVal);
                processRegularMsg(msgVal);
            }
        }
    }

    private void processEodMsg(int msgType) {
        if (!eodMsgExecutor.isShutdown()) {
            eodMsgExecutor.submit(() -> {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                msgQueue.add(msgType);
                logger.info("[E-Con] finish process EOD Msg: {}", msgType);
            });
        }
    }

    private void processRegularMsg(int msgType) {
        if (!regularMsgExecutor.isShutdown()) {
            regularMsgExecutor.submit(() -> {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                msgQueue.add(msgType);
                logger.info("[R-Con] finish process Regular Msg: {}", msgType);
            });
        }
    }

    @Override
    public void onApplicationEvent(MsgFlowEvent msgFlowEvent) {
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

    private synchronized void wakeBySow() {
        logger.info("SOW reach, recover receiver");
        eodSowSwitcher.set(false);
        this.notifyAll();
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
    }
}
