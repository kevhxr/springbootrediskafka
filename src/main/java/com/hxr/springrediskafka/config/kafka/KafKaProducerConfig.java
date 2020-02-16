package com.hxr.springrediskafka.config.kafka;


import com.hxr.springrediskafka.config.annotation.ConditionalOnSystemProperty;
import com.hxr.springrediskafka.util.KafkaSender;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ConditionalOnSystemProperty(name = "mode", value = "kafka")
@EnableKafka
public class KafKaProducerConfig {

    @Value("${kafka.bootstartpservers}")
    private String bootStrapServers;

    public static final String KAFKASENDER_BEAN="kafkaSender";


    @Bean
    public KafkaTemplate<String,String> kafkaTemplate(){
        return new KafkaTemplate(produceFactory());
    }

    @Bean
    public ProducerFactory<String, String> produceFactory() {
        return new DefaultKafkaProducerFactory<>(produceConfig());
    }


    @Bean
    public Map<String, Object> produceConfig() {
        Map<String, Object> configmap = new HashMap<>();
        configmap.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,bootStrapServers);
        configmap.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configmap.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return configmap;
    }

    @Bean(KAFKASENDER_BEAN)
    public KafkaSender kafkaSender(){
        return new KafkaSender();
    }
}
