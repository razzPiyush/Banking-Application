package com.banking.batch.TransactionScheduling.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/scheduler")
@RequiredArgsConstructor
public class SchedulerController {

    private final JobLauncher jobLauncher;
    private final Job loadJob;

    @GetMapping("/status")
    public ResponseEntity<String> getStatus() {
        return ResponseEntity.ok("Scheduler is running");
    }

    @GetMapping("/execute")
    public ResponseEntity<BatchStatus> executeBatchJob() {
        try {
            JobParameters parameters = createJobParameters();
            JobExecution jobExecution = jobLauncher.run(loadJob, parameters);
            
            log.info("Batch Job started with parameters: {}", parameters);
            
            while (jobExecution.isRunning()) {
                log.debug("Batch job is still running...");
                Thread.sleep(1000);
            }

            log.info("Batch Job completed with status: {}", jobExecution.getStatus());
            return ResponseEntity.ok(jobExecution.getStatus());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private JobParameters createJobParameters() {
        Map<String, JobParameter> params = new HashMap<>();
        params.put("time", new JobParameter(System.currentTimeMillis()));
        return new JobParameters(params);
    }

    @Scheduled(cron = "${scheduler.cron.expression:0 0 * * * ?}")
    public void scheduleJob() {
        try {
            executeBatchJob();
        } catch (Exception e) {
            log.error("Scheduled job failed: {}", e.getMessage());
        }
    }
}