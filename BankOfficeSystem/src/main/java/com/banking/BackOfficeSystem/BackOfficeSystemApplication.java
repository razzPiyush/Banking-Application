package com.banking.BackOfficeSystem;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

/**
 * Back Office System Application
 * 
 * This application handles administrative operations for a banking system with two main roles:
 * 
 * 1. Capturer:
 *    - Performs CRUD operations for customer accounts
 *    - Updates customer information as directed by Authoriser
 *    - Uses manual credentials for system access
 * 
 * 2. Authoriser:
 *    - Manages account creation approvals
 *    - Generates unique account numbers upon approval
 *    - Sends credentials via SMS to customers
 *    - Provides feedback for rejected applications
 */
@Slf4j
@SpringBootApplication
@EntityScan("com.common.BankData")
public class BackOfficeSystemApplication {

    public static void main(String[] args) {
        try {
            SpringApplication.run(BackOfficeSystemApplication.class, args);
            log.info("BankOffice Application Started Successfully");
        } catch (Exception e) {
            log.error("Failed to start BankOffice Application: {}", e.getMessage());
            System.exit(1);
        }
    }
}