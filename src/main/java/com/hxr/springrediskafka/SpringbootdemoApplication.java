package com.hxr.springrediskafka;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.hxr.springrediskafka.mapper")
public class SpringbootdemoApplication {

    static {
        System.setProperty("mode", "test");
    }

    public static void main(String[] args) {

        SpringApplication.run(SpringbootdemoApplication.class, args);
    }

}
