package com.banking.BackOfficeSystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan("com.common.BankData")
public class BackOfficeSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackOfficeSystemApplication.class, args);
        System.out.println("BackOffice Application Started");
    }
}

/*
CRUD operations of user account, and then send the notifications to the user via SMS
 */

/*
Bank office system
- handles adminstrative positions
- It enables administrators to create, manage, and approve customer accounts
- it has 2 roles - capturer and authoriser

Capturer-
- Inputs prospective customer details into the system.[CRUD operations for customers]
- Updates customer information upon request from the Authoriser. [follows commands of authoriser]
- Uses the system with manually entered credentials.

Authoriser-
- Only used in account creations
- Approves or declines account creation requests submitted by the Capturer.
- Upon approval:
  - Generates a unique account number. [Capturer inputs user details -> Authoriser creates an bacnk A/C number]
  - Sends login credentials to the customer via SMS. [fast2SMS API has been used]
- If declined, sends the request back to the Capturer with the reason for rejection.

 */


/*
- kya sirf bank account creation operation hi authoriser ke pas ja rha hai?
- Kya reject hone pe user ke pas SMS jayega?
- kya [update, delete] of bank account ka notification bhi user ke pas jayega?
 */

/*
Folder Structure

1) config
- Purpose: Contains configuration files related to security and request handling.
Files:
- AuthenticationFilter: Likely handles token-based or session-based authentication for requests.
- RequestFilter: Possibly a middleware to filter or preprocess incoming requests.
- SecurityConfig: Configures Spring Security to define authentication and authorization rules.

2) controller
- Purpose: Manages HTTP requests and routes them to appropriate services.
Files:
- AccountController: Handles account-related operations, such as creating or modifying customer accounts.
- AdminLoginController: Manages login functionality for the admin roles (Capturer and Authoriser).

3) service
- Purpose: Contains the business logic of the application.
Files:
- AuthenticationProvider: Likely validates admin credentials and provides authentication tokens.
- UserSecurityService: Handles user-related security functions, such as fetching user details or roles for authentication.
 */