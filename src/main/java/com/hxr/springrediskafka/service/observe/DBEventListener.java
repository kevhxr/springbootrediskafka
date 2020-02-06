package com.hxr.springrediskafka.service.observe;


import com.hxr.springrediskafka.entity.MsgFlowEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class DBEventListener {

    @Autowired
    ApplicationEventPublisher applicationEventPublisher;

    public void sendSowEvent(){
        MsgFlowEvent event = new MsgFlowEvent(this, "sow");
        applicationEventPublisher.publishEvent(event);
    }

    public void sendShutDownEvent(){
        MsgFlowEvent event = new MsgFlowEvent(this, "shutdown");
        applicationEventPublisher.publishEvent(event);
    }

    public void sendEodvent() {
        MsgFlowEvent event = new MsgFlowEvent(this, "eod");
        applicationEventPublisher.publishEvent(event);
    }
}
