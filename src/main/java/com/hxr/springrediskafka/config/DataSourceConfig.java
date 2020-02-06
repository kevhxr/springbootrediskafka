package com.hxr.springrediskafka.config;

import com.microsoft.sqlserver.jdbc.SQLServerDriver;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Bean("myDataSource")
    public DataSource getDataSource(){
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setUsername("xiaoran1");
        dataSource.setPassword("2222");
        dataSource.setDriverClassName(SQLServerDriver.class.getName());
        dataSource.setJdbcUrl("jdbc:sqlserver://PC201401120046:1433;databaseName=XrLearn");
        return dataSource;
    }
}
