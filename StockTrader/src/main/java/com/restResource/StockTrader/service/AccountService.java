package com.restResource.StockTrader.service;

import com.restResource.StockTrader.entity.converter.LocalDateTimeToEpochConverter;
import com.restResource.StockTrader.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AccountService {
    private AccountRepository accountRepository;

    private static LocalDateTimeToEpochConverter converter = new LocalDateTimeToEpochConverter();

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public void updateAccountBalance(String userId, int amount, int transactionNum) {
        accountRepository.updateAccountBalance(
                userId,
                amount,
                transactionNum,
                "TS1",
                converter.convertToDatabaseColumn(LocalDateTime.now()));
    }

    public Integer removeFunds(String userId, int amount, int transactionNum) {
        return accountRepository.removeFunds(
                userId,
                amount,
                transactionNum,
                "TS1",
                converter.convertToDatabaseColumn(LocalDateTime.now()));
    }

    public Boolean accountExists(String userId) {
        return accountRepository.accountExists(userId);
    }
}
