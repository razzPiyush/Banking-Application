package com.banking.batch.TransactionScheduling;

import com.common.BankData.BankDataApplication;
import com.common.BankData.dao.XConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Transaction Scheduling Microservice
 * 
 * This microservice handles batch processing and scheduling of banking transactions with the following
 * key functionalities:
 *
 * 1. Batch Processing:
 *    - Processes transactions in chunks using Spring Batch
 *    - Implements Reader-Processor-Writer pattern for data handling
 *    - Manages transaction state throughout processing
 *
 * 2. Transaction Scheduling:
 *    - Handles recurring transactions
 *    - Manages future-dated transactions
 *    - Implements CRON-based execution
 */
@Slf4j
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@Import({XConfiguration.class, BankDataApplication.class})
@EnableScheduling
public class TransactionSchedulingApplication {

    public static void main(String[] args) {
        try {
            SpringApplication.run(TransactionSchedulingApplication.class, args);
            log.info("Transaction Scheduling Application started successfully");
        } catch (Exception e) {
            log.error("Failed to start Transaction Scheduling Application: {}", e.getMessage());
            System.exit(1);
        }
    }
}