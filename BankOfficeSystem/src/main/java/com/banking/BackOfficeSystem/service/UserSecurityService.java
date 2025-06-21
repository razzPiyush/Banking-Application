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

/*
UserSecurityService.java:
- Implements Spring Security's UserDetailsService for admin authentication.
- Loads admin user details from the database using the provided username.
- Returns a CustomAdminDetails object if the admin exists, otherwise throws UsernameNotFoundException.
- Used by Spring Security during authentication to fetch user details for login and authorization checks.
*/

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