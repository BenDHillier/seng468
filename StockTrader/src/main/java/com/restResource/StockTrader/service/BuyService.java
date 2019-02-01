package com.restResource.StockTrader.service;

import com.restResource.StockTrader.entity.Quote;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class BuyService {

    private LoggingService logger;
    private QuoteService quoteService;
    private ThreadManager threadManager;

    public BuyService(LoggingService loggingService, QuoteService quoteService, ThreadManager threadManager){
        this.logger = loggingService;
        this.quoteService = quoteService;
        this.threadManager = threadManager;
    }

    @Async
    public CompletableFuture<Quote> getQuote(String stockSymbol, String userId) {
        Quote quote;
        synchronized(threadManager.getMap()) {
            if(!threadManager.getMap().containsKey(stockSymbol)) {
                quote = quoteService.getQuote(stockSymbol, userId);
                threadManager.getMap().put(stockSymbol, quote);
                return CompletableFuture.completedFuture(quote);
            } else{
                //need to check the date to make sure its ok and update if it isnt ok
                return CompletableFuture.completedFuture(threadManager.getMap().get(stockSymbol));
            }
        }
    }
}
