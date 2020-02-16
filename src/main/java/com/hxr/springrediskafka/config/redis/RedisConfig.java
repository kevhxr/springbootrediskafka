package com.hxr.springrediskafka.config.redis;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hxr.springrediskafka.config.annotation.ConditionalOnSystemProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;

@Configuration
@ConditionalOnSystemProperty(name = "mode", value = "redis")
public class RedisConfig {

    //@Value("${spring.redis.host}")
    private String redisHost = "192.168.1.101";

    //@Value("${spring.redis.port}")
    private int redisPort = 6379;

    //@Value("${spring.redis.timeout}")
    private int redisTimeout = 100000;

    //@Value("${spring.redis.password}")
    private String redisAuth = "1234";

    //@Value("${spring.redis.database}")
    private int redisDb;

    //@Value("${spring.redis.jedis.pool.max-active}")
    private int maxActive = 600;

    //@Value("${spring.redis.jedis.pool.max-wait}")
    private int maxWait = 3000;

    //@Value("${spring.redis.jedis.pool.max-idle}")
    private int maxIdle = 300;

    //@Value("${spring.redis.jedis.pool.min-idle}")
    private int minIdle = 0;


    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        //RedisConnectionFactory factory = createRedisConnFactory();
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(factory);//设置链接工厂

        RedisSerializer stringSerializer = new StringRedisSerializer();//初始化string序列化器

        //使用Jackson2JsonRedisSerializer来序列化和反序列化redis的value值（默认使用JDK的序列化方式）
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class); //初始化jackson序列化方式

        ObjectMapper om = new ObjectMapper(); // 指定要序列化的域，field,get和set,以及修饰符范围，ANY是都有包括private和public
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.PUBLIC_ONLY);
        // 指定序列化输入的类型，类必须是非final修饰的，final修饰的类，比如String,Integer等会跑出异常
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);

        redisTemplate.setKeySerializer(stringSerializer);//设置key使用string序列化方式
        redisTemplate.setHashKeySerializer(jackson2JsonRedisSerializer);//设置哈希键使用string的序列化方式
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);//设置
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);

        return redisTemplate;
    }


/*    public RedisConnectionFactory createRedisConnFactory() {

        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(maxActive);
        poolConfig.setMaxIdle(maxIdle);
        poolConfig.setMaxWaitMillis(maxWait);
        poolConfig.setMinIdle(minIdle);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(false);
        poolConfig.setTestWhileIdle(true);
        JedisClientConfiguration clientConfig = JedisClientConfiguration.builder()
                .usePooling().poolConfig(poolConfig).and().readTimeout(Duration.ofMillis(redisTimeout)).build();
        // 单点redis
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
        // 哨兵redis
        // RedisSentinelConfiguration redisConfig = new RedisSentinelConfiguration();
        // 集群redis
        // RedisClusterConfiguration redisConfig = new RedisClusterConfiguration();
        redisConfig.setHostName(redisHost);
        redisConfig.setPassword(RedisPassword.of(redisAuth));
        redisConfig.setPort(redisPort);
        //redisConfig.setDatabase(redisDb);

        return new JedisConnectionFactory(redisConfig, clientConfig);
    }*/
}
