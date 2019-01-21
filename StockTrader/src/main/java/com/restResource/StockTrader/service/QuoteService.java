package com.restResource.StockTrader.service;


import com.restResource.StockTrader.entity.Quote;

public interface QuoteService {
    Quote getQuote(String stockSymbol, String userId);
}
