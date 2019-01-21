package com.restResource.StockTrader.service;

import com.restResource.StockTrader.entity.Quote;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class MockQuoteService implements QuoteService {
    @Override
    public Quote getQuote(String stockSymbol, String userId) {
        return Quote.builder()
                .userId(userId)
                .stockSymbol(stockSymbol)
                .price(50)
                .key("abcdefg")
                .timestamp(LocalDateTime.now())
                .build();
    }
}
