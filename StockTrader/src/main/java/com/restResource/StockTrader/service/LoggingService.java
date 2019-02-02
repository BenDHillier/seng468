package com.restResource.StockTrader.service;

import com.restResource.StockTrader.entity.logging.ErrorEventLog;
import com.restResource.StockTrader.entity.logging.QuoteServerLog;
import com.restResource.StockTrader.entity.logging.UserCommandLog;
import com.restResource.StockTrader.repository.logging.ErrorEventLogRepository;
import com.restResource.StockTrader.repository.logging.QuoteServerLogRepository;
import com.restResource.StockTrader.repository.logging.UserCommandLogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LoggingService {

    private UserCommandLogRepository userCommandLogRepository;
    private QuoteServerLogRepository quoteServerLogRepository;
    private ErrorEventLogRepository errorEventLogRypository;

    public LoggingService(UserCommandLogRepository userCommandLogRepository,
                          QuoteServerLogRepository quoteServerLogRepository,
                          ErrorEventLogRepository errorEventLogRepository) {
        this.userCommandLogRepository = userCommandLogRepository;
        this.quoteServerLogRepository = quoteServerLogRepository;
        this.errorEventLogRypository = errorEventLogRepository;
    }

    public void logUserCommand(UserCommandLog log) {
        userCommandLogRepository.save(
                log.toBuilder()
                        .server("TS1")
                        .timestamp(LocalDateTime.now())
                        .build());


    }

    public void logQuoteServer(QuoteServerLog log) {
        quoteServerLogRepository.save(
                log.toBuilder()
                .build());
    }

    public void logErrorEvent(ErrorEventLog log) {
        errorEventLogRypository.save(
                log.toBuilder()
                        .timestamp(System.currentTimeMillis())
                        .server("TS1")
                        .build());
    }
}
