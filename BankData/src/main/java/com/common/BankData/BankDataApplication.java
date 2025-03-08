package com.common.BankData;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@Configuration
@ComponentScan
@EnableTransactionManagement
public class BankDataApplication {

	public static void main(String[] args) {
		SpringApplication.run(BankDataApplication.class, args);
	}

}
/*
The BankData module acts as a shared library (a .jar file), encapsulating common functionalities like utility functions,
data models, and validations, and is used across all microservices.
*/

/*
Bank Data (Features)

1) Admin and customer role security
CustomAdminDetails:
- Extends Admin and implements UserDetails.
- Overrides Spring Security methods like getAuthorities, getPassword, and getUsername.
- Assigns roles dynamically to admins (e.g., ROLE_ADMIN or ROLE_MANAGER).

2) Database Interaction:

- DAOs use JPA repositories or custom queries to interact with the database.
- Entities ensure proper table mapping for structured storage.

3) Transactional Services:

- TransferService and ScheduleDao suggest handling real-time and scheduled transactions between accounts.
- Uses DAO methods for persistent operations.

4) Authentication and Notifications:

- Integration with Spring Security for role-based access.
- SMS notifications for transactions (via SmsService).
 */



/*
Folder Structure

1) dao (Data Access Object):

- This package handles direct interaction with the database using entities and query methods.
- Contains DAO classes such as:
- AccountDao for managing account-related queries.
- AdminDao for admin-related operations.
- CustomerDao for customer data retrieval and manipulation.
- OtherBankAccountDao for handling inter-bank account data.
- ScheduleDao for scheduled transactions.
- TransferDao for fund transfer operations.
- Ensures modularized database interaction.

2) entity:

- Contains entity classes that map to database tables, such as:
- Security Entities: CustomAdminDetails, CustomCustomerDetails implementing security mechanisms (e.g., roles and authorities).
- Account, Customer, Admin: Represent user and account information.
- PrimaryTransaction, OtherAccount, and Proof: Represent financial transactions and identity proofs.
- ScheduleList: Represents scheduled operations.

3) service:

- Core business logic of the microservice.
- Key services include:
- AdminService: Admin-specific operations.
- AuthenticationService: Handles authentication flows.
- SmsService: Sends SMS notifications for account-related activities.
- TransferService: Manages transfer operations.
- UserSecurityService: Ensures secure access.
 */