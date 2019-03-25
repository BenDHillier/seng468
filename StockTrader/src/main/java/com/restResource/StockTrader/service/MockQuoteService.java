package com.restResource.StockTrader.service;

import com.restResource.StockTrader.entity.Quote;
import com.restResource.StockTrader.entity.logging.QuoteServerLog;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Profile("!prod")
public class MockQuoteService implements QuoteService {
    private LoggingService loggingService;
    private MockQuoteService(LoggingService loggingService) {
        this.loggingService = loggingService;
    }
    @Override
    public Optional<Quote> getQuote(String stockSymbol, String userId, int transactionNum) {
        loggingService.logQuoteServer(Long.toString(System.currentTimeMillis()), "QS1",Integer.toString(transactionNum),"50","S",userId,Long.toString(System.currentTimeMillis()),"made_up_cryptokey");
        return Optional.of(Quote.builder()
                .userId(userId)
                .stockSymbol(stockSymbol)
                .price(50)
                .cryptoKey("hd19dg29fj1772nd10")
                .timestamp(LocalDateTime.now())
                .build());
    }
}