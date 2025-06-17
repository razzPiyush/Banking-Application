package com.banking.OnlineBanking.controller;

import com.common.BankData.dao.ScheduleDao;
import com.common.BankData.entity.Schedule;
import com.common.BankData.entity.ScheduleList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final ScheduleDao scheduleDao;

    @PostMapping("/scheduleTransaction")
    public ResponseEntity<String> scheduleJob(@Validated @RequestBody ScheduleList scheduleList) {
        try {
            if (scheduleList == null || scheduleList.getSchedule() == null || scheduleList.getSchedule().isEmpty()) {
                return ResponseEntity.badRequest().body("Schedule list cannot be empty");
            }

            processSchedules(scheduleList);
            return ResponseEntity.ok("Transactions scheduled successfully");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to schedule transactions");
        }
    }

    /**
     * Process multiple schedules from the provided list
     */
    private void processSchedules(ScheduleList scheduleList) {
        for (Schedule schedule : scheduleList.getSchedule()) {
            try {
                validateAndSaveSchedule(schedule);
            } catch (Exception e) {
                log.error("Error processing schedule: {}", e.getMessage());
            }
        }
    }

    /**
     * Validates and saves a single schedule entry
     */
    private void validateAndSaveSchedule(Schedule schedule) {
        if (isValidSchedule(schedule)) {
            schedule.setStatus("scheduled");
            scheduleDao.save(schedule);
            log.info("Schedule saved successfully: {}", schedule.getScheduleid());
        } else {
            log.warn("Invalid schedule detected and skipped");
        }
    }

    /**
     * Checks if schedule has valid account IDs and positive amount
     */
    private boolean isValidSchedule(Schedule schedule) {
        return schedule != null 
            && schedule.getAccountId() != 0 
            && schedule.getRecipientAccountNo() != 0
            && schedule.getAmount() > 0;
    }
}