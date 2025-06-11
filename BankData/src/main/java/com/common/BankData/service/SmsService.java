package com.common.BankData.service;

public interface SmsService {
    void sendSms(String phoneNumber, String message);
    void sendTransactionSms(String phoneNumber, double amount, String type);
}