package com.common.BankData.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.SecureRandom;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmsService {

    // Custom exception for SMS errors
    public static class SmsException extends RuntimeException {
        public SmsException(String message) {
            super(message);
        }
        public SmsException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    @Value("${sms.api.key}")
    private String apiKey;

    @Value("${sms.sender.id:FSTSMS}")
    private String senderId;

    public void sendSms(long username, String number, String password) {
        try {
            String url = buildSmsUrl(username, number, password);
            String response = sendHttpRequest(url);
            log.info("SMS sent successfully to {}", number);
            log.debug("SMS API Response: {}", response);
        } catch (Exception e) {
            log.error("Failed to send SMS to {}: {}", number, e.getMessage());
            throw new SmsException("Failed to send SMS", e);
        }
    }

    private String buildSmsUrl(long username, String number, String password) {
        return UriComponentsBuilder.fromHttpUrl("https://www.fast2sms.com/dev/bulk")
                .queryParam("authorization", apiKey)
                .queryParam("sender_id", senderId)
                .queryParam("language", "english")
                .queryParam("route", "qt")
                .queryParam("numbers", number)
                .queryParam("message", "27480")
                .queryParam("variables", "{AA}|{BB}")
                .queryParam("variables_values", username + "|" + password)
                .build()
                .toUriString();
    }

    private String sendHttpRequest(String urlString) throws Exception {
        URL url = java.net.URI.create(urlString).toURL();
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0");
        conn.setRequestProperty("cache-control", "no-cache");

        if (conn.getResponseCode() != 200) {
            throw new SmsException("SMS API returned error code: " + conn.getResponseCode());
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            return reader.lines().collect(Collectors.joining());
        }
    }

    public static char[] generatePassword(int length) {
        if (length < 4) {
            throw new IllegalArgumentException("Password length must be at least 4 characters");
        }

        String capitalLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerLetters = "abcdefghijklmnopqrstuvwxyz";
        String specialChars = "!@#$";
        String numbers = "1234567890";
        String allChars = capitalLetters + lowerLetters + specialChars + numbers;

        SecureRandom random = new SecureRandom();
        char[] password = new char[length];

        // Ensure password contains required character types
        password[0] = lowerLetters.charAt(random.nextInt(lowerLetters.length()));
        password[1] = capitalLetters.charAt(random.nextInt(capitalLetters.length()));
        password[2] = specialChars.charAt(random.nextInt(specialChars.length()));
        password[3] = numbers.charAt(random.nextInt(numbers.length()));

        // Fill remaining length with random characters
        for (int i = 4; i < length; i++) {
            password[i] = allChars.charAt(random.nextInt(allChars.length()));
        }

        return password;
    }
}