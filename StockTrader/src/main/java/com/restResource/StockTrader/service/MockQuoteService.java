package com.restResource.StockTrader.service;

import com.restResource.StockTrader.entity.CommandType;
import com.restResource.StockTrader.entity.Quote;
import com.restResource.StockTrader.entity.logging.QuoteServerLog;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class MockQuoteService implements QuoteService {
    private LoggingService loggingService;
    private MockQuoteService(LoggingService loggingService) {
        this.loggingService = loggingService;
    }
    @Override
    public Quote getQuote(String stockSymbol, String userId) {

        loggingService.logQuoteServer(50,stockSymbol,userId,System.currentTimeMillis(),"hd19dg29fj1772nd10");
        loggingService.logSystemEvent(CommandType.QUOTE,userId,stockSymbol,null,null);
        return Quote.builder()
                .userId(userId)
                .stockSymbol(stockSymbol)
                .price(50)
                .key("hd19dg29fj1772nd10")
                .timestamp(LocalDateTime.now())
                .build();
    }
}
