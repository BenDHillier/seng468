package com.restResource.StockTrader.service;

import org.springframework.stereotype.Service;

@Service
public class MockQuoteService implements QuoteService {
    @Override
    public int getQuote(String stockSymbol, String userId) {
        return 10;
    }
}
