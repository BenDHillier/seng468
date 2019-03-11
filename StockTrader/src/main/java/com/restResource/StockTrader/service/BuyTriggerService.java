package com.restResource.StockTrader.service;

import com.restResource.StockTrader.entity.BuyTrigger;
import com.restResource.StockTrader.entity.CommandType;
import com.restResource.StockTrader.entity.Quote;
import com.restResource.StockTrader.entity.logging.ErrorEventLog;
import com.restResource.StockTrader.service.AccountService;
import com.restResource.StockTrader.repository.BuyTriggerRepository;
import com.restResource.StockTrader.repository.InvestmentRepository;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BuyTriggerService {

    private QuoteService quoteService;
    private BuyTriggerRepository buyTriggerRepository;
    private TaskExecutor taskExecutor;
    private AccountService accountService;
    private InvestmentRepository investmentRepository;
    private LoggingService loggingService;

    public BuyTriggerService(LoggingService loggingService,
                             QuoteService quoteService,
                             BuyTriggerRepository buyTriggerRepository,
                             TaskExecutor taskExecutor,
                             InvestmentRepository investmentRepository,
                             AccountService accountService){
        this.quoteService = quoteService;
        this.buyTriggerRepository = buyTriggerRepository;
        this.taskExecutor = taskExecutor;
        this.accountService = accountService;
        this.investmentRepository = investmentRepository;
        this.loggingService = loggingService;
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
                    Optional<BuyTrigger> buyStockSnapshot = buyTriggerRepository.findByUserIdAndStockSymbol(userId, stockSymbol);
                    if (buyStockSnapshot.isPresent()) {
                        cost = buyStockSnapshot.get().getStockCost();
                        amount = buyStockSnapshot.get().getStockAmount();
                    } else { //the trigger has been deleted -> not needed if we are removing the thread using the id
                        return;
                    }
                    Optional<Quote> optionalQuote = quoteService.getQuote(stockSymbol, userId, transactionNum);
                    if (!optionalQuote.isPresent()) {
                        return;
                    }
                    Quote quote = optionalQuote.get();
                    //TODO handle quote not existing
                    if (quote.getPrice() <= cost) {
                        Integer refund = amount % quote.getPrice();
                        if (refund > 0) {
                            //if the user wanted to buy for cheaper then the cost, refund the difference
                            accountService.updateAccountBalance(userId, refund, transactionNum);
                        }
                        investmentRepository.insertOrIncrement(userId, stockSymbol, amount);
                        buyTriggerRepository.delete(buyStockSnapshot.get());
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