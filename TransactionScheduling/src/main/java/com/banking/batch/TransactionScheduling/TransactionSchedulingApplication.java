package com.banking.batch.TransactionScheduling;

import com.common.BankData.BankDataApplication;
import com.common.BankData.dao.XConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@Import({XConfiguration.class, BankDataApplication.class})
@EnableScheduling
public class TransactionSchedulingApplication {

	public static void main(String[] args) {
		SpringApplication.run(TransactionSchedulingApplication.class, args);
	}

}

/*
Functionalities Provided by the TransactionScheduling Microservice

1) Batch Processing:
- Uses Spring Batch to process large datasets (e.g., transactions) in chunks.
- The pipeline involves:
- Reader: Fetches unprocessed or scheduled transactions from the database.
- Processor: Validates, enriches, or modifies data before processing.
- Writer: Saves the processed data back to the database.

2) Scheduled Transactions:
- Handles recurring or future-dated transactions, ensuring they are executed on the specified schedule.
- Uses CronService for periodic execution.

3) Job Scheduling and Monitoring:
- Allows manual or automated triggering of batch jobs via the SchedulerController.
- Enables monitoring of job execution status.

4) Database Interaction:
- Reads and writes transaction data using the configured database through DBConfig and DBWriter.
 */



/*
Integration with Other Microservices

1) OnlineBanking:
- Receives input from the OnlineBanking microservice for scheduled fund transfers.
- Processes these inputs and ensures execution at the scheduled time.

2) BankData:
- Likely interacts with the BankData microservice for fetching account details and validating data before
  processing transactions.
 */

/*
Sub-Packages and Files

1) config:
- Contains configuration classes for setting up batch jobs, database interaction, and processing logic.
- BatchConfiguration: Configures batch job settings such as job steps, readers, processors, and writers.
- DBConfig: Contains database connection configurations for accessing data related to transactions.
- DBWriter: Writes processed transaction data (e.g., scheduled transactions) into the database.
- UserItemProcessor: Processes input data (e.g., user or transaction details) and applies business logic
  (e.g., validation, formatting).

2) controller:
- SchedulerController: Manages API endpoints for scheduling or triggering batch processes manually or via external
  triggers.

3) service:
- Contains the core business logic for scheduling and processing transactions.
- CronService: Handles periodic tasks using CRON jobs, such as executing scheduled transactions at specific intervals.
- TransactionSchedulingApplication: The main class to bootstrap the microservice.
 */