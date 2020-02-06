package com.hxr.springrediskafka.service;

import com.hxr.springrediskafka.entity.MsgFlowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;

@Service
@DependsOn(value = {"eodMsgExecutor","regularMsgExecutor"})
public class MsgHandlerService implements ApplicationListener<MsgFlowEvent> {

    Logger logger = LoggerFactory.getLogger(MsgHandlerService.class);

    @Resource(name = "eodMsgExecutor")
    private ExecutorService eodMsgExecutor;

    @Resource(name = "regularMsgExecutor")
    private ExecutorService regularMsgExecutor;

    private boolean eodSowSwitcher = false;
    private Thread thread;
    private boolean terminated = false;

    private Queue<Integer> msgQueue = new LinkedBlockingQueue<>();

    public MsgHandlerService(ExecutorService eodMsgExecutor, ExecutorService regularMsgExecutor) {
        this.eodMsgExecutor = eodMsgExecutor;
        this.regularMsgExecutor = regularMsgExecutor;
    }


    @PostConstruct
    public void startUp(){
        thread = new Thread(this::onMessage);
        thread.setName("messageReceiver01");
        thread.start();
    }

    public void onMessage() {
        int msg = 0;
        logger.info("Start to receive message from main processor!!!");
        while (!terminated) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            logger.info("[Producer] produce new msg: {}", msg);
            msgQueue.add(msg);
            msg++;
            processMessage();
        }
        for (Integer integer : msgQueue) {
            System.out.print(integer + ",");
        }
        eodMsgExecutor.shutdown();
        regularMsgExecutor.shutdown();
    }

    public void processMessage() {
        if (!eodSowSwitcher) {
            int msgVal = msgQueue.remove();
            if (msgVal < 0) {
                logger.info("[E-Consumer] EOD message reached");
                eodSowSwitcher = true;
                processEodMsg(msgVal);
            } else {
                logger.info("[R-Consumer] Regular message reached");
                processRegularMsg(msgVal);
            }
        }
    }

    private void processEodMsg(int msgType) {
        if (!eodMsgExecutor.isShutdown()) {
            eodMsgExecutor.submit(() -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                logger.info("[E-Consumer] received and processed EODMsg: {}", msgType);
            });
        }
    }

    private void processRegularMsg(int msgType) {
        if (!regularMsgExecutor.isShutdown()) {
            regularMsgExecutor.submit(() -> {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                logger.info("[R-Consumer] received and processed Regular Msg: {}", msgType);
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

    private void wakeBySow() {
        logger.info("SOW reach, recover receiver");
        eodSowSwitcher = false;
    }

    private void eodReached() {
        logger.info("EOD reach, stop receiver");
        msgQueue.add(-1);
    }

    private void doShutDown() {
        logger.info("Shutdown command reach, put an end to the whole flow");
        terminated = true;
        this.eodMsgExecutor.shutdown();
        this.regularMsgExecutor.shutdown();
    }
}
