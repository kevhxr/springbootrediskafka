package com.hxr.springrediskafka.config;

import com.hxr.springrediskafka.util.ExecutorPoolUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;

@Configuration
public class MsgFlowConfig {

    @Bean(value = "eodMsgExecutor")
    ExecutorService getEodMsgExecutor(){
        return ExecutorPoolUtil.generateThreadPool();
    }


    @Bean(value = "regularMsgExecutor")
    ExecutorService getNormalMsgExecutor(){
        return ExecutorPoolUtil.generateThreadPool();
    }

}
