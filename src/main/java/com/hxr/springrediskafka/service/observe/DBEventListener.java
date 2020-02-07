package com.hxr.springrediskafka.service.observe;


import com.hxr.springrediskafka.entity.event.MsgFlowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class DBEventListener {

    Logger logger = LoggerFactory.getLogger(DBEventListener.class);
    @Autowired
    ApplicationEventPublisher applicationEventPublisher;

    public void sendSowEvent(){
        logger.info("sendSowEvent");
        MsgFlowEvent event = new MsgFlowEvent(this, "sow");
        applicationEventPublisher.publishEvent(event);
    }

    public void sendShutDownEvent(){
        logger.info("sendShutDownEvent");
        MsgFlowEvent event = new MsgFlowEvent(this, "shutdown");
        applicationEventPublisher.publishEvent(event);
    }

    public void sendEodvent() {
        logger.info("sendEodvent");
        MsgFlowEvent event = new MsgFlowEvent(this, "eod");
        applicationEventPublisher.publishEvent(event);
    }
}
