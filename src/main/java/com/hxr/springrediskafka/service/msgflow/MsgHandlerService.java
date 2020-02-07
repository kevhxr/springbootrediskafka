package com.hxr.springrediskafka.service.msgflow;

import com.hxr.springrediskafka.config.annotation.ConditionalOnSystemProperty;
import com.hxr.springrediskafka.config.kafka.KafKaProducerConfig;
import com.hxr.springrediskafka.util.KafkaSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Random;

@Service
@ConditionalOnSystemProperty(name = "mode", value = "Prod")
public class MsgHandlerService {

    Logger logger = LoggerFactory.getLogger(MsgHandlerService.class);

    public final static String MSG_TOPIC = "msgtp02";

    @Resource(name = KafKaProducerConfig.KAFKASENDER_BEAN)
    private KafkaSender kafkaSender;

    private Thread thread;
    private boolean terminated = false;

    @PostConstruct
    public void startUp() {
        thread = new Thread(this::onMessage);
        thread.setName("messageReceiver01");
        thread.start();
    }

    public void onMessage() {
        int msg = 0;
        Random random = new Random();
        logger.info("Start to receive message from main processor!!!");
        while (!terminated) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            int randInt = random.nextInt(3);
/*            if (randInt > 1) {
                logger.info("[Producer] produce new msg: {}", msg);
                kafkaSender.send("" + msg, MSG_TOPIC);
                msg++;
            }*/
        }
    }

    public void doShutDown() {
        terminated = true;
    }


}
