package com.hxr.springrediskafka.config.kafka;


import com.hxr.springrediskafka.config.annotation.ConditionalOnSystemProperty;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
@ConditionalOnSystemProperty(name = "mode", value = "kafka")
public class KafKaConsumerConfig {

    @Value("${kafka.bootstartpservers}")
    private String bootStrapServers;


    @Bean(value = "consumerFactory")
    public ConsumerFactory<String, String> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfig());
    }


    @Bean
    public Map<String, Object> consumerConfig() {
        Map<String, Object> configMap = new HashMap<>();
        configMap.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootStrapServers);
        configMap.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configMap.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configMap.put(ConsumerConfig.GROUP_ID_CONFIG, "batch");
        configMap.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        //configMap.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "3000");
        configMap.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG,"10000");
        configMap.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "10");
        configMap.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, "2");

        configMap.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");

        return configMap;
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory factory = new ConcurrentKafkaListenerContainerFactory();
        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(2);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        factory.setBatchListener(true);
        return factory;
    }
}
