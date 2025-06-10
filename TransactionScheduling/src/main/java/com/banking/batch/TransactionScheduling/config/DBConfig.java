package com.banking.batch.TransactionScheduling.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class DBConfig {

    private final Environment environment;

    private static final String DRIVER_CLASS = "spring.datasource.driver-class-name";
    private static final String URL = "spring.datasource.url";
    private static final String USERNAME = "spring.datasource.username";
    private static final String PASSWORD = "spring.datasource.password";

    @Bean
    @Primary
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(getRequiredProperty(DRIVER_CLASS));
        dataSource.setUrl(getRequiredProperty(URL));
        dataSource.setUsername(getRequiredProperty(USERNAME));
        dataSource.setPassword(getRequiredProperty(PASSWORD));
        return dataSource;
    }

    @Bean
    @Primary
    public PlatformTransactionManager transactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setDataSource(dataSource());
        return transactionManager;
    }

    private String getRequiredProperty(String key) {
        String value = environment.getProperty(key);
        if (value == null) {
            throw new IllegalStateException("Required database property " + key + " is not set");
        }
        return value;
    }
}