package com.restResource.StockTrader.service;

import com.restResource.StockTrader.entity.Quote;
import com.restResource.StockTrader.entity.SellTrigger;
import com.restResource.StockTrader.repository.AccountRepository;
import com.restResource.StockTrader.repository.InvestmentRepository;
import com.restResource.StockTrader.repository.SellTriggerRepository;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SellTriggerService {

    private QuoteService quoteService;
    private SellTriggerRepository sellTriggerRepository;
    private TaskExecutor taskExecutor;
    private AccountRepository accountRepository;
    private InvestmentRepository investmentRepository;
    private LoggingService loggingService;

    public SellTriggerService(LoggingService loggingService,
                             QuoteService quoteService,
                             SellTriggerRepository sellTriggerRepository,
                             InvestmentRepository investmentRepository,
                             TaskExecutor taskExecutor,
                             AccountRepository accountRepository){
        this.quoteService = quoteService;
        this.sellTriggerRepository = sellTriggerRepository;
        this.taskExecutor = taskExecutor;
        this.accountRepository = accountRepository;
        this.loggingService = loggingService;
        this.investmentRepository = investmentRepository;
    }

    public void start(String userId, String stockSymbol, Integer stockCost, Integer transactionNum) {
        taskExecutor.execute(new Runnable() {
            @Override
            public void run(){

                //TODO technically can assign this from initialization
                Integer cost;
                Integer amount;
                for (;;) {
                    //TODO its possible avoid this db query, by saving the thread id to the db and deleteing the thread using that
                    Optional<SellTrigger> sellStockSnapshot = sellTriggerRepository.findByUserIdAndStockSymbol(userId, stockSymbol);
                    if (sellStockSnapshot.isPresent()) {
                        cost = sellStockSnapshot.get().getStockCost();
                        amount = sellStockSnapshot.get().getStockAmount();
                    } else { //the trigger has been deleted -> not needed if we are removing the thread using the id
                        return;
                    }
                    Optional<Quote> optionalQuote = quoteService.getQuote(stockSymbol, userId, transactionNum);
                    if (!optionalQuote.isPresent()) {
                        return;
                    }
                    Quote quote = optionalQuote.get();
                    if (quote.getPrice() >= cost) {
                        int stocksToSell = amount / quote.getPrice();
                        int stocksReserved = amount / cost;
                        Integer profit = quote.getPrice() * stocksToSell;
                        if (stocksReserved > stocksToSell) {
                            investmentRepository.insertOrIncrement(userId, stockSymbol, stocksReserved - stocksToSell);
                        }
                        accountRepository.updateAccountBalance(userId, profit, transactionNum, "TS1");
                        sellTriggerRepository.delete(sellStockSnapshot.get());
                        return;
                    }
                    try {
                        //TODO might want the 60 seconds part of the config
                        Thread.sleep(60000); //we sleep for 60 seconds every time, since we make the buy decision immediately
                    } catch(InterruptedException e) {
                        throw new IllegalArgumentException(
                                "Error with Thread.sleep (60 seconds) in "+Thread.currentThread().getName());
                    }

                }

            }
        });
    }
}