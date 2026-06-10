package com.multiservicio.config;

import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HibernateConfig {

    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer() {
        return properties -> {
            properties.put("hibernate.hbm2ddl.auto", "none");
            properties.put("jakarta.persistence.schema-generation.database.action", "none");
            properties.put("jakarta.persistence.schema-generation.scripts.action", "none");
            properties.put("hibernate.schema_update.unique_constraint_strategy", "skip");
        };
    }
}
