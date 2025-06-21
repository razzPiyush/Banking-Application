package com.banking.batch.TransactionScheduling.config;

import com.common.BankData.entity.Schedule;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.MapJobRepositoryFactoryBean;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.orm.jpa.JpaTransactionManager;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

// WORKFLOW OF BATCH CONFIGURATION
/*
1. INITIALIZATION
   - Application starts with @EnableBatchProcessing
   - Constructor injects required dependencies:
     * JobBuilderFactory: Creates batch jobs
     * StepBuilderFactory: Creates steps
     * DataSource: Database connection
     * JpaTransactionManager: Handles transactions
     * DBWriter: Custom writer for processing transactions

2. JOB REPOSITORY SETUP
   - createJobRepository() creates in-memory storage for job metadata
   - Stores execution status, parameters, and metrics

3. READER CONFIGURATION (reader() method)
   - Creates JDBC reader to fetch schedules from database
   - Uses SQL query: SELECT scheduleid, accountid, amount...
   - Maps database rows to Schedule objects using ScheduleRowMapper
   - Reads records one by one using cursor

4. PROCESSOR SETUP (processor() method)
   - Creates UserItemProcessor for pre-processing schedules
   - Can perform validation or data transformation
   - Processes each schedule before writing

5. STEP CONFIGURATION (processSchedulesStep() method)
   - Creates a step that:
     a. Reads 10 schedules at a time (CHUNK_SIZE)
     b. Processes each schedule
     c. Writes/executes processed schedules
   - Manages transactions for each chunk
   - Uses reader → processor → writer chain

6. JOB CONFIGURATION (scheduleProcessingJob() method)
   - Creates the main batch job
   - Uses RunIdIncrementer for unique job instances
   - Sets up flow with processing step
   - Job executes the complete batch process

7. DATA MAPPING (ScheduleRowMapper class)
   - Maps database columns to Schedule object properties
   - Handles data type conversion
   - Creates Schedule objects from ResultSet

EXECUTION FLOW:
Reader → Processor → Writer (in chunks of 10)
│         │           │
│         │           └─ DBWriter executes transactions
│         └─ UserItemProcessor pre-processes
└─ JDBC Reader fetches from database

ERROR HANDLING:
- Transactions ensure atomicity
- Failed chunks can be rolled back
- Job repository tracks execution status
*/

/**
 * Configuration class for Spring Batch processing of scheduled transactions
 * Handles the setup of batch jobs, steps, and related components
 */
@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class BatchConfiguration extends DefaultBatchConfigurer {

    // Required components injected through constructor
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DataSource dataSource;
    private final JpaTransactionManager transactionManager;
    private final DBWriter dbWriter;

    // Constants for batch processing
    private static final int CHUNK_SIZE = 10;  // Number of items to process in each chunk
    private static final String SCHEDULE_QUERY =
            "SELECT scheduleid, accountid, amount, dates, " +
            "recipientaccountno, recipientName, status, type " +
            "FROM schedule";

    /**
     * Creates an in-memory job repository for storing job execution metadata
     */
    @Override
    @org.springframework.lang.NonNull
    protected JobRepository createJobRepository() throws Exception {
        MapJobRepositoryFactoryBean factoryBean = new MapJobRepositoryFactoryBean();
        factoryBean.afterPropertiesSet();
        return factoryBean.getObject();
    }

    /**
     * Configures the item reader to fetch schedules from the database
     * Uses JDBC cursor to read records one at a time
     */
    @Bean
    public JdbcCursorItemReader<Schedule> reader() {
        JdbcCursorItemReader<Schedule> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(dataSource);
        reader.setSql(SCHEDULE_QUERY);
        reader.setRowMapper(new ScheduleRowMapper());
        return reader;
    }

    /**
     * Creates the item processor bean for any pre-processing of schedules
     */
    @Bean
    public UserItemProcessor processor() {
        return new UserItemProcessor();
    }

    /**
     * Configures the batch processing step
     * Combines reader, processor and writer in a transactional chunk-oriented step
     */
    @Bean
    public Step processSchedulesStep() {
        return stepBuilderFactory.get("processSchedules")
                .transactionManager(transactionManager)
                .<Schedule, Schedule>chunk(CHUNK_SIZE)
                .reader(reader())
                .processor(processor())
                .writer(dbWriter)
                .build();
    }

    /**
     * Defines the batch job configuration
     * Sets up the job flow with the processing step
     */
    @Bean
    public Job scheduleProcessingJob() {
        return jobBuilderFactory.get("scheduleProcessingJob")
                .incrementer(new RunIdIncrementer())
                .flow(processSchedulesStep())
                .end()
                .build();
    }

    /**
     * Inner class to map database rows to Schedule objects
     * Implements Spring's RowMapper interface
     */
    private static class ScheduleRowMapper implements RowMapper<Schedule> {
        @Override
        public Schedule mapRow(@org.springframework.lang.NonNull ResultSet rs, int rowNum) throws SQLException {
            Schedule schedule = new Schedule();
            // Map database columns to Schedule object properties
            schedule.setScheduleid(rs.getInt("scheduleid"));
            schedule.setAccountId(rs.getLong("accountid"));
            schedule.setAmount(rs.getFloat("amount"));
            schedule.setDates(rs.getDate("dates"));
            schedule.setRecipientAccountNo(rs.getLong("recipientaccountno"));
            schedule.setRecipientName(rs.getString("recipientName"));
            schedule.setStatus(rs.getString("status"));
            schedule.setType(rs.getString("type"));
            return schedule;
        }
    }
}