package com.hxr.springrediskafka.config;


import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@ConfigurationProperties("mybatis")
@MapperScan(
        basePackages = DBConfig.BASE_PACKAGE,
        sqlSessionFactoryRef = DBConfig.SSF_BEAN_NAME)
public class DBConfig extends AbstractDBConfig {

    public static final String DATASOURCE_NAME = "myDataSource";
    public static final String PTM_BEAN_NAME = "myPlatformTransactionManager";
    public static final String SST_BEAN_NAME = "mySqlSessionTemplate";
    public static final String SSF_BEAN_NAME = "sqlSessionFactory";
    public static final String BASE_PACKAGE = "com.hxr.springrediskafka.mapper";

    private String[] mapperLocations;
    private String typeAliasesPackage;

    @Autowired
    @Qualifier(DATASOURCE_NAME)
    DataSource dataSource;

    public String getTypeAliasesPackage() {
        return typeAliasesPackage;
    }

    public void setTypeAliasesPackage(String typeAliasesPackage) {
        this.typeAliasesPackage = typeAliasesPackage;
    }

    public String[] getMapperLocations() {
        return mapperLocations;
    }

    public void setMapperLocations(String[] mapperLocations) {
        this.mapperLocations = mapperLocations;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    @Bean(SST_BEAN_NAME)
    public SqlSessionTemplate getSqlSessionTemplate(@Qualifier(SSF_BEAN_NAME) SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    @Bean(PTM_BEAN_NAME)
    public PlatformTransactionManager getDataSourceManager(@Qualifier(DATASOURCE_NAME) DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(SSF_BEAN_NAME)
    public SqlSessionFactory getSqlSessionFactory() throws Exception {
        SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
        Resource[] resources = getMapperResources(getMapperLocations());
        DataSource dataSource = getDataSource();

        sessionFactoryBean.setDataSource(dataSource);
        sessionFactoryBean.setConfiguration(getConfiguration());
        sessionFactoryBean.setTypeAliasesPackage(getTypeAliasesPackage());
        sessionFactoryBean.setMapperLocations(resources);
        sessionFactoryBean.setFailFast(true);
        return sessionFactoryBean.getObject();
    }
}
