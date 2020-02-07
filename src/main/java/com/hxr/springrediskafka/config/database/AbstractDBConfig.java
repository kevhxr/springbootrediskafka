package com.hxr.springrediskafka.config.database;

import org.apache.ibatis.logging.slf4j.Slf4jImpl;
import org.apache.ibatis.session.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AbstractDBConfig {

    protected Configuration getConfiguration() {
        Configuration configuration = new Configuration();
        configuration.setUseActualParamName(true);
        configuration.setUseColumnLabel(true);
        configuration.setMapUnderscoreToCamelCase(true);
        configuration.setUseGeneratedKeys(true);
        configuration.setLazyLoadingEnabled(true);
        configuration.setAggressiveLazyLoading(true);
        configuration.setCacheEnabled(true);
        configuration.setLogImpl(Slf4jImpl.class);
        return configuration;
    }



    protected Resource[] getMapperResources(String[] locations) {
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        return Arrays.asList(locations).stream().map(e -> {
            List<Resource> resources = new ArrayList<>();
            try {
                List<Resource> rcList = Arrays.asList(resolver.getResources(e));
                resources.addAll(rcList);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return resources;
        }).flatMap(ls -> ls.stream()).toArray(Resource[]::new);
    }
}
