package com.common.BankData.service;

import com.common.BankData.dao.*;
import com.common.BankData.entity.*;
import com.common.BankData.exception.InsufficientFundsException;
import com.common.BankData.exception.TransferException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransferService {

    private final AccountDao accountDao;
    private final OtherBankAccountDao otherBankAccountDao;
    private final TransferDao transferDao;
    private final ScheduleDao scheduleDao;

    @Transactional
    public boolean addMoneyToRecipient(Account recipientAccount, Account primaryAccount, 
            double amount, PrimaryTransaction transaction) {
        
        try {
            validateTransfer(primaryAccount, amount);
            
            executeTransfer(recipientAccount, primaryAccount, amount);
            
            createAndSaveTransaction(transaction);
            
            log.info("Transfer completed: {} to account {}", 
                    amount, recipientAccount.getAccountId());
            return true;
            
        } catch (InsufficientFundsException e) {
            log.warn("Insufficient funds for transfer: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("Transfer failed: {}", e.getMessage());
            throw new TransferException("Transfer failed", e);
        }
    }

    @Transactional
    public boolean addMoneyToRecipientOfAnotherBank(OtherAccount recipientAccount, 
            Account primaryAccount, double amount, PrimaryTransaction transaction) {
        
        try {
            validateTransfer(primaryAccount, amount);
            
            executeInterBankTransfer(recipientAccount, primaryAccount, amount);
            
            createAndSaveTransaction(transaction);
            
            log.info("Inter-bank transfer completed: {} to account {}", 
                    amount, recipientAccount.getAccountId());
            return true;
            
        } catch (InsufficientFundsException e) {
            log.warn("Insufficient funds for inter-bank transfer: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("Inter-bank transfer failed: {}", e.getMessage());
            throw new TransferException("Inter-bank transfer failed", e);
        }
    }

    @Transactional(readOnly = true)
    public Set<PrimaryTransaction> getTransactionHistoryByAccountID(long accountId) {
        Set<PrimaryTransaction> transactions = new HashSet<>();
        transactions.addAll(transferDao.findByAccountId(accountId));
        transactions.addAll(transferDao.findByRecipientAccountNo(accountId));
        return transactions;
    }

    @Transactional
    public void deleteSchedule(Schedule schedule) {
        try {
            int deleted = scheduleDao.removeByScheduleid(schedule.getScheduleid());
            if (deleted == 0) {
                throw new TransferException("Schedule not found: " + schedule.getScheduleid());
            }
            log.info("Schedule deleted: {}", schedule.getScheduleid());
        } catch (Exception e) {
            log.error("Failed to delete schedule: {}", e.getMessage());
            throw new TransferException("Failed to delete schedule", e);
        }
    }

    private void validateTransfer(Account primaryAccount, double amount) {
        double balanceAfterTransfer = primaryAccount.getBalance() - amount;
        if (balanceAfterTransfer <= 0) {
            throw new InsufficientFundsException("Insufficient funds for transfer");
        }
    }

    private void executeTransfer(Account recipientAccount, Account primaryAccount, double amount) {
        recipientAccount.setBalance(recipientAccount.getBalance() + amount);
        primaryAccount.setBalance(primaryAccount.getBalance() - amount);
        
        accountDao.save(recipientAccount);
        accountDao.save(primaryAccount);
    }

    private void executeInterBankTransfer(OtherAccount recipientAccount, 
            Account primaryAccount, double amount) {
        recipientAccount.setBalance(recipientAccount.getBalance() + amount);
        primaryAccount.setBalance(primaryAccount.getBalance() - amount);
        
        otherBankAccountDao.save(recipientAccount);
        accountDao.save(primaryAccount);
    }

    private PrimaryTransaction createAndSaveTransaction(PrimaryTransaction transaction) {
        PrimaryTransaction newTransaction = new PrimaryTransaction(
            new Date(),
            transaction.getDescription(),
            "completed",
            transaction.getAmount(),
            transaction.getRecipientName(),
            transaction.getRecipientAccountNo(),
            transaction.getAccountId(),
            LocalDateTime.now(Clock.systemUTC()),
            transaction.getType()
        );
        return transferDao.save(newTransaction);
    }
}