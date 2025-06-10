package com.banking.OnlineBanking.controller;

import com.common.BankData.dao.AccountDao;
import com.common.BankData.entity.Account;
import com.common.BankData.entity.OtherAccount;
import com.common.BankData.entity.PrimaryTransaction;
import com.common.BankData.service.TransferService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/transfer")
@RequiredArgsConstructor
public class TransferController {

    private final AccountDao accountDao;
    private final TransferService transferService;

    @PostMapping("/betweenAccounts")
    public ResponseEntity<String> betweenAccounts(@Validated @RequestBody PrimaryTransaction transaction) {
        try {
            Account recipientAccount = accountDao.findByAccountId(transaction.getRecipientAccountNo());
            Account primaryAccount = accountDao.findByAccountId(transaction.getAccountId());

            if (primaryAccount == null) {
                return ResponseEntity.badRequest().body("Source account not found");
            }

            if (recipientAccount != null) {
                processInternalTransfer(recipientAccount, primaryAccount, transaction);
            } else {
                processExternalTransfer(transaction, primaryAccount);
            }

            return ResponseEntity.ok("Transfer completed successfully");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Transfer failed: " + e.getMessage());
        }
    }

    @GetMapping("/transactionHistory/{accountId}")
    public ResponseEntity<List<PrimaryTransaction>> getTransactionList(@PathVariable long accountId) {
        try {
            Set<PrimaryTransaction> transactions = transferService.getTransactionHistoryByAccountID(accountId);
            return ResponseEntity.ok(new ArrayList<>(transactions));
        } catch (Exception e) {
            log.error("Error fetching transaction history for account {}: {}", accountId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/balance/{accountId}")
    public ResponseEntity<Account> getBalance(@PathVariable long accountId) {
        try {
            Account account = accountDao.findByAccountId(accountId);
            if (account == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(account);
        } catch (Exception e) {
            log.error("Error fetching balance for account {}: {}", accountId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/balanceAmountOnly/{accountId}")
    public ResponseEntity<Double> balanceAmountOnly(@PathVariable long accountId) {
        try {
            Account account = accountDao.findByAccountId(accountId);
            if (account == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(account.getBalance());
        } catch (Exception e) {
            log.error("Error fetching balance amount for account {}: {}", accountId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private void processInternalTransfer(Account recipientAccount, Account primaryAccount, 
                                       PrimaryTransaction transaction) {
        transferService.addMoneyToRecipient(
            recipientAccount, 
            primaryAccount, 
            transaction.getAmount(), 
            transaction
        );
    }

    private void processExternalTransfer(PrimaryTransaction transaction, Account primaryAccount) {
        OtherAccount recipientAccount = new OtherAccount(
            transaction.getAmount(),
            new java.sql.Date(System.currentTimeMillis()),
            transaction.getRecipientAccountNo()
        );

        transferService.addMoneyToRecipientOfAnotherBank(
            recipientAccount,
            primaryAccount,
            transaction.getAmount(),
            transaction
        );
    }
}