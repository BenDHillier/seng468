package com.restResource.StockTrader.service;


import com.restResource.StockTrader.entity.Quote;

import java.util.Optional;

public interface QuoteService {
    Optional<Quote> getQuote(String stockSymbol, String userId, int transactionNum);
}
