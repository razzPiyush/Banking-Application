package com.banking.BackOfficeSystem.service;

import com.common.BankData.dao.AdminDao;
import com.common.BankData.entity.Admin;
import com.common.BankData.entity.CustomAdminDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserSecurityService implements UserDetailsService {

    private static final String USER_NOT_FOUND_MESSAGE = "Username %s not found";
    
    private final AdminDao adminDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            Admin admin = adminDao.findByUserName(username);
            if (admin != null) {
                return new CustomAdminDetails(admin);
            } else {
                throw createNotFoundException(username);
            }
        } catch (Exception e) {
            log.error("Error loading user {}: {}", username, e.getMessage());
            throw new UsernameNotFoundException(String.format(USER_NOT_FOUND_MESSAGE, username), e);
        }
    }

    private UsernameNotFoundException createNotFoundException(String username) {
        String message = String.format(USER_NOT_FOUND_MESSAGE, username);
        log.warn(message);
        return new UsernameNotFoundException(message);
    }
}