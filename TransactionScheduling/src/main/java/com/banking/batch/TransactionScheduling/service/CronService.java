package com.banking.batch.TransactionScheduling.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/*
 * Difference Between CronService and SchedulerController
 * ===================================================
 *
 * 1. CronService
 *    Purpose: Automated background processing
 *    - Runs automatically every 60 seconds
 *    - No manual intervention needed
 *    - Used for scheduled transactions
 *    - Background service (no API endpoints)
 *    Example: Daily scheduled transfers at specific times
 *
 * 2. SchedulerController
 *    Purpose: Manual control and monitoring
 *    - Triggered by HTTP API calls
 *    - Requires manual intervention
 *    - Used for administrative control
 *    - Exposes REST endpoints
 *    Example: Admin manually triggering pending transactions
 *
 * When to Use Which:
 * - CronService: For automated, time-based processing
 * - SchedulerController: For manual control and monitoring
 *
 * Note: Both components work together but serve different purposes
 * in the transaction processing system.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CronService {

    private final JobLauncher jobLauncher;
    private final Job loadJob;

    private static final long FIXED_RATE = 60000; // 1 minute in milliseconds

    @Scheduled(fixedRate = FIXED_RATE)
    public BatchStatus load() {
        try {
            JobParameters parameters = createJobParameters();
            JobExecution jobExecution = jobLauncher.run(loadJob, parameters);
            
            log.info("Batch job started at: {}", parameters.getParameters().get("time"));
            
            while (jobExecution.isRunning()) {
                log.debug("Batch job is still running...");
                Thread.sleep(1000);
            }
            
            log.info("Batch job completed with status: {}", jobExecution.getStatus());
            return jobExecution.getStatus();

        } catch (Exception e) {
            log.error("Error executing batch job: {}", e.getMessage());
            return BatchStatus.FAILED;
        }
    }

    private JobParameters createJobParameters() {
        Map<String, JobParameter> params = new HashMap<>();
        params.put("time", new JobParameter(System.currentTimeMillis()));
        return new JobParameters(params);
    }
}