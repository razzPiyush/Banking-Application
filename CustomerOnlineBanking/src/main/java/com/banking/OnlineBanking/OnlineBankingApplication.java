package com.banking.OnlineBanking;

import com.common.BankData.BankDataApplication;
import com.common.BankData.dao.XConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Online Banking Application
 *
 * This microservice provides online banking functionalities with the following key features:
 *
 * 1. Customer Operations:
 *    - Account management and transaction history
 *    - Personal information updates
 *    - Balance inquiries and statements
 *
 * 2. Security:
 *    - JWT-based authentication
 *    - Role-based access control
 *    - Session management
 *
 * 3. Fund Transfers:
 *    - Real-time transfers
 *    - Scheduled transactions
 *    - Transaction validation
 */
@Slf4j
@SpringBootApplication
@EntityScan("com.common.BankData")
@EnableScheduling
@Import({XConfiguration.class, BankDataApplication.class})
public class OnlineBankingApplication {

    public static void main(String[] args) {
        try {
            SpringApplication.run(OnlineBankingApplication.class, args);
            log.info("Customer Online Banking Application started successfully");
        } catch (Exception e) {
            log.error("Failed to start Customer Online Banking Application: {}", e.getMessage());
            System.exit(1);
        }
    }
}