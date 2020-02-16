package com.hxr.springrediskafka.config.kafka;


import com.hxr.springrediskafka.config.annotation.ConditionalOnSystemProperty;
import com.hxr.springrediskafka.entity.kafka.TradeMsg;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
@ConditionalOnSystemProperty(name = "mode", value = "kafkaj")
public class KafKaConsumerJsonConfig {

    @Value("${kafka.bootstartpservers}")
    private String bootStrapServers;


    @Bean
    public ConsumerFactory<Integer, TradeMsg> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfig(), new IntegerDeserializer(),
                new JsonDeserializer<>(TradeMsg.class));
    }


    @Bean
    public Map<String, Object> consumerConfig() {
        Map<String, Object> configMap = new HashMap<>();
        configMap.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootStrapServers);
        configMap.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class);
        configMap.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        configMap.put(ConsumerConfig.GROUP_ID_CONFIG, "jsonbatch");
        configMap.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        //configMap.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "3000");
        configMap.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, "1000");
        configMap.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "10");
        configMap.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, "2");

        configMap.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        return configMap;
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<Integer, TradeMsg>> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<Integer, TradeMsg> factory =
                new ConcurrentKafkaListenerContainerFactory();
        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(2);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        factory.setBatchListener(true);
        return factory;
    }
}
