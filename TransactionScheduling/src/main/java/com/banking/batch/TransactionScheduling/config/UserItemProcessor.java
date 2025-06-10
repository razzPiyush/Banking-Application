package com.banking.batch.TransactionScheduling.config;

import com.common.BankData.entity.Schedule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class UserItemProcessor implements ItemProcessor<Schedule, Schedule> {

    @Override
    public Schedule process(@org.springframework.lang.NonNull Schedule schedule) throws Exception {
        try {
            if (isValidSchedule(schedule)) {
                log.debug("Processing schedule: {}", schedule.getScheduleid());
                return enrichSchedule(schedule);
            }
            return null; // Skip invalid schedules
        } catch (Exception e) {
            log.error("Error processing schedule {}: {}", schedule.getScheduleid(), e.getMessage());
            throw e;
        }
    }

    private boolean isValidSchedule(Schedule schedule) {
        return schedule != null 
            && schedule.getAccountId() != 0 
            && schedule.getRecipientAccountNo() != 0
            && schedule.getAmount() > 0;
    }

    private Schedule enrichSchedule(Schedule schedule) {
        schedule.setLastProcessedDate(new Date());
        schedule.setStatus("PROCESSING");
        return schedule;
    }
}