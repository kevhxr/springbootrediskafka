package com.hxr.springrediskafka.config.annotation;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.MultiValueMap;

public class OnSystemPropertyCondition implements Condition {
    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata metadata) {

        MultiValueMap<String, Object> attributes =
                metadata.getAllAnnotationAttributes(ConditionalOnSystemProperty.class.getName());
        if(attributes!=null){
            String name = attributes.get("name").get(0).toString();
            String value = attributes.get("value").get(0).toString();
            String property = System.getProperty(name);
            if (property.equals(value)) {
                return true;
            }
        }
        return false;
    }
}
