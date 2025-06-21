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
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmsServiceImpl implements SmsService {

    @Value("${sms.api.key}")
    private String apiKey;

    @Value("${sms.sender.id:FSTSMS}")
    private String senderId;

    @Override
    public void sendSms(String phoneNumber, String message) {
        try {
            String url = buildSmsUrl(phoneNumber, message);
            String response = sendHttpRequest(url);
            log.info("SMS sent successfully to {}", phoneNumber);
            log.debug("SMS API Response: {}", response);
        } catch (Exception e) {
            log.error("Failed to send SMS to {}: {}", phoneNumber, e.getMessage());
            throw new SmsException("Failed to send SMS", e);
        }
    }

    @Override
    public void sendTransactionSms(String phoneNumber, double amount, String type) {
        String message = String.format("Transaction %s completed. Amount: %.2f", type, amount);
        sendSms(phoneNumber, message);
    }

    private String buildSmsUrl(String number, String message) {
        return UriComponentsBuilder.fromHttpUrl("https://www.fast2sms.com/dev/bulk")
                .queryParam("authorization", apiKey)
                .queryParam("sender_id", senderId)
                .queryParam("language", "english")
                .queryParam("route", "qt")
                .queryParam("numbers", number)
                .queryParam("message", message)
                .build()
                .toUriString();
    }

    private String sendHttpRequest(String urlString) throws Exception {
        URL url = new java.net.URI(urlString).toURL();
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0");
        conn.setRequestProperty("cache-control", "no-cache");

        if (conn.getResponseCode() != 200) {
            throw new SmsException("SMS API returned error code: " + conn.getResponseCode());
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            return reader.lines().collect(Collectors.joining());
        }
    }

    public static class SmsException extends RuntimeException {
        public SmsException(String message) {
            super(message);
        }
        
        public SmsException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}