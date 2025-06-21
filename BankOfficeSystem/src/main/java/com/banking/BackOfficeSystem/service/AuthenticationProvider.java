package com.banking.BackOfficeSystem.service;

import com.common.BankData.dao.AdminDao;
import com.common.BankData.entity.Admin;
import com.common.BankData.entity.CustomAdminDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.apache.commons.lang3.StringUtils;

/*
AuthenticationProvider.java:
- Custom Spring Security authentication provider for admin users.
- Authenticates requests using a token (not username/password).
- On authentication, extracts the token from the request, finds the admin in the database using AdminDao, and returns user details if valid.
- Throws authentication exceptions if the token is missing, invalid, or the admin is not found.
- Used by Spring Security to secure protected endpoints, ensuring only valid admins with correct tokens can access admin resources.
*/

@Slf4j
@Component
public class AuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    private static final String INVALID_TOKEN_MESSAGE = "Invalid authentication token";
    private static final String USER_NOT_FOUND_MESSAGE = "User not found with token: %s";

    @Autowired
    private AdminDao adminDao;

    @Override
    protected void additionalAuthenticationChecks(
            UserDetails userDetails, 
            UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        // Additional security checks can be implemented here
        if (userDetails == null || authentication == null) {
            throw new BadCredentialsException(INVALID_TOKEN_MESSAGE);
        }
    }

    @Override
    protected UserDetails retrieveUser(
            String username, 
            UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        try {
            String token = extractToken(authentication);
            Admin admin = findAdminByToken(token);
            return new CustomAdminDetails(admin);
        } catch (Exception e) {
            log.error("Authentication failed: {}", e.getMessage());
            throw new UsernameNotFoundException(e.getMessage());
        }
    }

    private String extractToken(UsernamePasswordAuthenticationToken authentication) {
        Object token = authentication.getCredentials();
        if (token == null || StringUtils.isBlank(token.toString())) {
            throw new BadCredentialsException(INVALID_TOKEN_MESSAGE);
        }
        return token.toString();
    }

    private Admin findAdminByToken(String token) {
        Admin admin = adminDao.findByToken(token);
        if (admin == null) {
            String message = String.format(USER_NOT_FOUND_MESSAGE, token);
            log.warn(message);
            throw new UsernameNotFoundException(message);
        }
        return admin;
    }
}