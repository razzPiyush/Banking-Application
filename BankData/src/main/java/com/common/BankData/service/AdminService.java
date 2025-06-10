package com.common.BankData.service;

import com.common.BankData.dao.AdminDao;
import com.common.BankData.entity.Admin;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {

    // Custom exception for admin not found
    public static class AdminNotFoundException extends RuntimeException {
        public AdminNotFoundException(String message) {
            super(message);
        }
    }

    private final AdminDao adminDao;

    @Transactional(readOnly = true)
    public Admin findByToken(String token) {
        log.debug("Finding admin by token: {}", token);
        Admin admin = adminDao.findByToken(token);
        if (admin == null) {
            throw new AdminNotFoundException("Admin not found for token: " + token);
        }
        return admin;
    }

    @Transactional(readOnly = true)
    public Admin findByUsername(String username) {
        log.debug("Finding admin by username: {}", username);
        Admin admin = adminDao.findByUserName(username);
        if (admin == null) {
            throw new AdminNotFoundException("Admin not found for username: " + username);
        }
        return admin;
    }
}