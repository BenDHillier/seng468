package com.restResource.StockTrader.service;


public interface QuoteService {
    int getQuote(String stockSymbol, String userId);
}
