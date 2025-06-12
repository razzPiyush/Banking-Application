package com.banking.BackOfficeSystem.controller;

import com.common.BankData.dao.AdminDao;
import com.common.BankData.entity.Admin;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/login/api")
public class AdminLoginController {

    private static final String AUTH_HEADER_PREFIX = "Basic ";
    private static final String UNAUTHORIZED_MESSAGE = "Invalid credentials";

    @Autowired
    private AdminDao adminDao;

    @PostMapping("/secured/token")
    public ResponseEntity<Object> generateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            String[] credentials = extractCredentials(authHeader);
            String username = credentials[0].toLowerCase();
            String password = credentials[1];

            Admin admin = validateAndGetAdmin(username, password);
            String token = generateAndSaveToken(admin);

            // You can return just the token, or a map with both admin and token if needed
            return ResponseEntity.ok()
                    .body(token);

        } catch (IllegalArgumentException e) {
            log.warn("Invalid authorization header format");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid authorization header format");
        } catch (SecurityException e) {
            log.warn("Authentication failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(UNAUTHORIZED_MESSAGE);
        } catch (Exception e) {
            log.error("Error during authentication", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal server error");
        }
    }

    private String[] extractCredentials(String authHeader) {
        if (authHeader == null || !authHeader.startsWith(AUTH_HEADER_PREFIX)) {
            throw new IllegalArgumentException("Invalid authorization header");
        }

        String base64Credentials = authHeader.substring(AUTH_HEADER_PREFIX.length()).trim();
        String credentials = new String(Base64.getDecoder().decode(base64Credentials), StandardCharsets.UTF_8);
        return credentials.split(":", 2);
    }

    private Admin validateAndGetAdmin(String username, String password) throws NoSuchAlgorithmException {
        Admin admin = adminDao.findByUserName(username);
        if (admin == null || !isPasswordValid(password, admin.getPasswordHash())) {
            throw new SecurityException("Invalid username or password");
        }
        return admin;
    }

    private boolean isPasswordValid(String password, String storedHash) throws NoSuchAlgorithmException {
        String hashedPassword = hashPassword(password);
        return hashedPassword.equals(storedHash);
    }

    private String generateAndSaveToken(Admin admin) {
        String token = UUID.randomUUID().toString();
        admin.setToken(token);
        adminDao.save(admin);
        return token;
    }

    private String hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] digest = md.digest(password.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(digest);
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}