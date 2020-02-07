package com.hxr.springrediskafka.entity.event;

import org.springframework.context.ApplicationEvent;

public class MsgFlowEvent extends ApplicationEvent {

    public final static String SOW_EVENT = "sow";
    public final static String SHUTDOWN_EVENT = "shutdown";
    public final static String EOD_EVENT = "eod";
    private static final long serialVersionUID = 5135227192045756396L;
    private String message;

    public MsgFlowEvent(Object source, String message) {
        super(source);
        this.message = message;
    }

    @Override
    public Object getSource() {
        return message;
    }
}
