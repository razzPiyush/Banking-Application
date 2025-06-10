package com.common.BankData.service;

import com.common.BankData.dao.CustomerDao;
import com.common.BankData.entity.CustomCustomerDetails;
import com.common.BankData.entity.Customer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    private final CustomerDao customerDao;

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, 
            UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        // No additional checks needed for token-based authentication
    }

    @Override
    protected UserDetails retrieveUser(String userName, 
            UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        
        try {
            Object token = authentication.getCredentials();
            log.debug("Attempting to authenticate user with token");
            
            Customer customer = customerDao.findByToken(token.toString());
            if (customer == null) {
                throw new UsernameNotFoundException(
                    "Cannot find user with authentication token=" + token);
            }
            return new CustomCustomerDetails(customer);
                    
        } catch (Exception e) {
            log.error("Authentication failed: {}", e.getMessage());
            throw new AuthenticationException("Authentication failed") {};
        }
    }
}