package com.restResource.StockTrader.service;

import com.restResource.StockTrader.entity.logging.UserCommandLog;
import com.restResource.StockTrader.repository.logging.UserCommandLogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LoggingService {

    private UserCommandLogRepository userCommandLogRepository;

    public LoggingService(UserCommandLogRepository userCommandLogRepository) {
        this.userCommandLogRepository = userCommandLogRepository;
    }

    public void logUserCommand(UserCommandLog log) {
        userCommandLogRepository.save(
                log.toBuilder()
                        .server("SERV1")
                        .timestamp(LocalDateTime.now())
                        .build());


    }
}
