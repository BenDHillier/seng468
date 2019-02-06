package com.restResource.StockTrader.service;

import com.restResource.StockTrader.entity.BuyTrigger;
import com.restResource.StockTrader.entity.Quote;
import com.restResource.StockTrader.repository.BuyTriggerRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@Service
@EnableAsync
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
    @Transactional
    public void start(String stockSymbol, String userId) { //used to return a quote object but changed it to void since we never return
        try {
            while (true) {
                //CompletableFuture<Optional<BuyTrigger>> k = callDB(stockSymbol, userId);
                Optional<BuyTrigger> k = buyTriggerRepository.findByUserIdAndStockSymbol(userId, stockSymbol);
                if(k.isPresent()){
                    System.out.println(k.get().isExpired());

                }else{
                    System.out.println("Not working");
                }

                Thread.sleep(5000);
            }
        } catch(InterruptedException e){
            throw new IllegalArgumentException("Error throw in thread managing " + userId + " and "+stockSymbol + "\n"+ e);
        } catch(Exception e){
            throw new IllegalArgumentException(e);
        }

    }

    @Transactional
    public Optional<BuyTrigger> callDB(String stockSymbol, String userId){
        return buyTriggerRepository.findByUserIdAndStockSymbol(userId, stockSymbol);
    }
}