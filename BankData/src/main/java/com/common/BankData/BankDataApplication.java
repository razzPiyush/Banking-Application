package com.common.BankData;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Main entry point for the BankData shared library.
 * This module provides common functionality for the banking microservices including:
 * - Security (Admin/Customer roles)
 * - Database interactions
 * - Transaction management
 * - Authentication
 * - Notifications
 */
@SpringBootApplication
@Configuration
@ComponentScan
@EnableTransactionManagement
public class BankDataApplication {

    public static void main(String[] args) {
        SpringApplication.run(BankDataApplication.class, args);
    }

}