package com.banking.batch.TransactionScheduling.config;

import com.common.BankData.dao.AccountDao;
import com.common.BankData.dao.ScheduleDao;
import com.common.BankData.entity.Account;
import com.common.BankData.entity.PrimaryTransaction;
import com.common.BankData.entity.Schedule;
import com.common.BankData.service.TransferService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/*
 * DBWRITER WORKFLOW
 * ================
 * 1. Input: Receives batch of scheduled transactions
 * 2. Process Flow:
 *    → Check if schedule is due today
 *    → Verify accounts exist (sender & receiver)
 *    → Create transaction record
 *    → Execute money transfer
 *    → Update schedule status
 * 
 * 3. Key Methods:
 *    - write(): Entry point, processes batch of schedules
 *    - processSchedule(): Handles single schedule
 *    - executeTransfer(): Performs money transfer
 *    - completeSchedule(): Marks success
 *    - markScheduleFailed(): Marks failure
 * 
 * 4. Error Handling:
 *    - Transaction rollback on failure
 *    - Logging at each step
 *    - Failed schedules marked for retry
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DBWriter implements ItemWriter<Schedule> {

    private final ScheduleDao scheduleDao;
    private final AccountDao accountDao;
    private final TransferService transferService;

    @Override
    @Transactional
    public void write(@org.springframework.lang.NonNull List<? extends Schedule> schedules) throws Exception {
        Date today = new Date();
        
        for (Schedule schedule : schedules) {
            if (!DateUtils.isSameDay(today, schedule.getDates())) {
                continue;
            }
            
            processSchedule(schedule, today);
        }
    }

    private void processSchedule(Schedule schedule, Date executionDate) {
        try {
            Account recipientAccount = accountDao.findByAccountId(schedule.getRecipientAccountNo());
            if (recipientAccount == null) {
                log.error("Recipient account not found: {}", schedule.getRecipientAccountNo());
                markScheduleFailed(schedule);
                return;
            }

            Account primaryAccount = accountDao.findByAccountId(schedule.getAccountId());
            if (primaryAccount == null) {
                log.error("Primary account not found: {}", schedule.getAccountId());
                markScheduleFailed(schedule);
                return;
            }

            PrimaryTransaction transaction = createTransaction(schedule, executionDate);
            
            if (executeTransfer(recipientAccount, primaryAccount, schedule, transaction)) {
                completeSchedule(schedule);
            } else {
                markScheduleFailed(schedule);
            }
        } catch (Exception e) {
            log.error("Error processing schedule {}: {}", schedule.getScheduleid(), e.getMessage());
            markScheduleFailed(schedule);
        }
    }

    private PrimaryTransaction createTransaction(Schedule schedule, Date executionDate) {
        return new PrimaryTransaction(
            executionDate,
            "Scheduled Transaction",
            "null",
            schedule.getAmount(),
            schedule.getRecipientName(),
            schedule.getRecipientAccountNo(),
            schedule.getAccountId(),
            null,
            schedule.getType()
        );
    }

    private boolean executeTransfer(Account recipientAccount, Account primaryAccount, 
                                  Schedule schedule, PrimaryTransaction transaction) {
        try {
            return transferService.addMoneyToRecipient(
                recipientAccount,
                primaryAccount,
                schedule.getAmount(),
                transaction
            );
        } catch (Exception e) {
            log.error("Error executing transfer for schedule {}: {}", schedule.getScheduleid(), e.getMessage());
            return false;
        }
    }

    private void completeSchedule(Schedule schedule) {
        schedule.setStatus("completed");
        scheduleDao.delete(schedule);
        log.info("Schedule {} completed successfully", schedule.getScheduleid());
    }

    private void markScheduleFailed(Schedule schedule) {
        schedule.setStatus("failed");
        scheduleDao.save(schedule);
        log.warn("Schedule {} failed to process", schedule.getScheduleid());
    }
}