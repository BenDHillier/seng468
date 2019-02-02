package com.restResource.StockTrader.service;

import com.restResource.StockTrader.entity.BuyTrigger;
import com.restResource.StockTrader.entity.Quote;
import com.restResource.StockTrader.repository.BuyTriggerRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class BuyTriggerService {

    private LoggingService logger;
    private QuoteService quoteService;
    private BuyTriggerRepository buyTriggerRepository;

    public BuyTriggerService(LoggingService loggingService, QuoteService quoteService, BuyTriggerRepository buyTriggerRepository){
        this.logger = loggingService;
        this.quoteService = quoteService;
        this.buyTriggerRepository = buyTriggerRepository;
    }

    @Async
    public CompletableFuture<Quote> start(String stockSymbol, String userId) {
        try {
            while (true) {
                Optional<BuyTrigger> k = buyTriggerRepository.findByUserIdAndStockSymbol(userId, stockSymbol);
                Thread.sleep(5000);
                if(k.isPresent()){
                    System.out.println(k.get().isExpired());

                }else{
                    System.out.println("Not working");
                }
            }
        } catch(InterruptedException e){
            throw new IllegalArgumentException("Error throw in thread managing " + userId + " and "+stockSymbol + "\n"+ e);
        } catch(Exception e){
            throw new IllegalArgumentException(e);
        }
    }
}
