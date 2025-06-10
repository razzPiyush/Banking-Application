package com.banking.OnlineBanking.controller;

import com.common.BankData.dao.CustomerDao;
import com.common.BankData.entity.Customer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/login/customer/api")
@RequiredArgsConstructor
public class CustomerLoginController {

    private static final String AUTH_PREFIX = "Basic ";
    private static final String USER_NOT_FOUND = "Username not found";
    private static final String INVALID_CREDENTIALS = "Invalid username or password";

    private final CustomerDao customerDao;

    @PostMapping("/secured/token")
    public ResponseEntity<Object> generateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            String[] credentials = extractCredentials(authHeader);
            long userId = Long.parseLong(credentials[0]);
            String password = credentials[1];

            return authenticateAndGenerateToken(userId, password);

        } catch (NumberFormatException e) {
            log.error("Invalid user ID format: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Invalid user ID format");
        } catch (IllegalArgumentException e) {
            log.error("Authentication error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            log.error("Login error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    private String[] extractCredentials(String authHeader) {
        if (authHeader == null || !authHeader.startsWith(AUTH_PREFIX)) {
            throw new IllegalArgumentException("Invalid authorization header");
        }

        String base64Credentials = authHeader.substring(AUTH_PREFIX.length()).trim();
        String credentials = new String(Base64.getDecoder().decode(base64Credentials));
        return credentials.split(":", 2);
    }

    private ResponseEntity<Object> authenticateAndGenerateToken(long userId, String password) {
        Customer customer = customerDao.findByUserId(userId);
        
        if (customer == null) {
            log.warn("Login attempt with non-existent user ID: {}", userId);
            throw new IllegalArgumentException(USER_NOT_FOUND);
        }

        if (!customer.getPassword().equals(password)) {
            log.warn("Invalid password attempt for user ID: {}", userId);
            throw new IllegalArgumentException(INVALID_CREDENTIALS);
        }

        String token = UUID.randomUUID().toString();
        customer.setToken(token);
        customerDao.save(customer);
        
        log.info("Successful login for user ID: {}", userId);
        return ResponseEntity.ok(customer);
    }
}