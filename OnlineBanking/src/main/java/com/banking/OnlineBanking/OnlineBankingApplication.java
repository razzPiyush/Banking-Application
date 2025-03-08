package com.banking.OnlineBanking;

import com.common.BankData.BankDataApplication;
import com.common.BankData.dao.XConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EntityScan("com.common.BankData")
@EnableScheduling
@Import({XConfiguration.class, BankDataApplication.class})
public class OnlineBankingApplication {

	public static void main(String[] args) {
		SpringApplication.run(OnlineBankingApplication.class, args);
	}

}

/*
Functionalities-

1) Customer Operations:
- View account details and transaction history.
- Update personal details (if supported).

2) Authentication:
- Secure login/logout using Spring Security and authentication filters.

3) Fund Transfers:
- Real-time and scheduled fund transfers through TransferController.

4) Security and Request Validation:
- Ensures all operations are secure with role-based access control.
 */



/*
Inter-Service Interaction

- Integration with BankData Microservice:
- The OnlineBanking service interacts with the BankData microservice to fetch account details, transactions,
  and customer information.
- Uses @EntityScan to directly map and use BankData entities.

TransactionScheduling Microservice:
- The TransactionScheduling service is also present, handling scheduled fund transfers or periodic operations.
- Likely interacts with Online Banking for validation and execution.
 */

/*
Sub-Packages and Files

1) config:
- Contains configuration files and classes for authentication, security, and request handling.
- AuthenticationFilter: Likely intercepts requests to validate and authenticate users before processing.
- RequestFilter: Applies filters for specific request parameters or pre-processing logic (e.g., logging or validating input).
- SecurityConfig: Configures Spring Security for role-based access control, password encryption, and other security measures.

2) controller:

- This package handles API requests and routes them to appropriate services for processing.
- CustomerController: Manages customer-related operations like account viewing, updating details, or retrieving transactions.
- CustomerLoginController: Handles login/logout and session management for customers.
- TransferController: Manages fund transfers between accounts, including validations and notifications.
 */