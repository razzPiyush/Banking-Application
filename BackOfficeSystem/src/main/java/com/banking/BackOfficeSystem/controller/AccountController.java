package com.banking.BackOfficeSystem.controller;

import com.common.BankData.dao.AccountDao;
import com.common.BankData.dao.CustomerDao;
import com.common.BankData.entity.Account;
import com.common.BankData.entity.Customer;
import com.common.BankData.service.SmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/accounts")
public class AccountController {

    @Autowired
    private AccountDao accountDao;

    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private SmsService smsService;

    @GetMapping("/getAll")
    public ResponseEntity<List<Account>> getAllAccounts() {
        try {
            List<Account> accounts = accountDao.findAll();
            if (accounts.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(accounts);
        } catch (Exception e) {
            log.error("Error fetching all accounts: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/add")
    public ResponseEntity<Account> addAccount(@RequestBody Account account) {
        try {
            Account savedAccount = accountDao.save(account);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedAccount);
        } catch (Exception e) {
            log.error("Error creating account: ", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<Account> getById(@PathVariable Long id) {
        try {
            return accountDao.findById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error fetching account with id {}: ", id, e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Account> updateAccount(@RequestBody Account account, @PathVariable long id) {
        try {
            account.setId(id);
            
            // Handle new approved account
            if (account.getAccountStatus() == 3 && account.getAccountId() == 0) {
                generateNewAccountId(account);
            }

            // Save account and create customer if needed
            Account updatedAccount = accountDao.saveAndFlush(account);
            if (updatedAccount != null && account.getAccountStatus() == 3) {
                createCustomerAndSendCredentials(updatedAccount);
            }

            return ResponseEntity.ok(updatedAccount);
        } catch (Exception e) {
            log.error("Error updating account with id {}: ", id, e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/getAllByStatus/{status}")
    public ResponseEntity<List<Account>> getAllByStatus(@PathVariable int status) {
        try {
            List<Account> accounts = accountDao.findByAccountStatus(status);
            if (accounts.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(accounts);
        } catch (Exception e) {
            log.error("Error fetching accounts with status {}: ", status, e);
            return ResponseEntity.badRequest().build();
        }
    }

    private void generateNewAccountId(Account account) {
        long accountId;
        do {
            accountId = (long) (Math.random() * 100000000 * 1000000);
        } while (accountDao.findByAccountId(accountId) != null);
        
        account.setAccountId(accountId);
        account.setAccountStatus(3);
    }

    private void createCustomerAndSendCredentials(Account account) {
        try {
            long customerId = customerDao.getNextCustomerId();
            String password = new String(SmsService.generatePassword(10));

            Set<Account> accountSet = new HashSet<>();
            accountSet.add(account);

            Customer customer = new Customer(
                    account.getFirstName() + account.getLastName(),
                    account.getFirstName(),
                    password,
                    accountSet,
                    null,
                    customerId
            );

            customerDao.save(customer);
            smsService.sendSms(customerId, String.valueOf(account.getPhoneNo()), password);
        } catch (Exception e) {
            log.error("Error creating customer for account {}: ", account.getId(), e);
            throw e;
        }
    }
}