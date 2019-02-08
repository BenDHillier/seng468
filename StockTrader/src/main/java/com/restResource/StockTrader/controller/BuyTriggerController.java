package com.restResource.StockTrader.controller;

import com.restResource.StockTrader.entity.BuyTrigger;
import com.restResource.StockTrader.entity.CommandType;
import com.restResource.StockTrader.entity.PendingBuy;
import com.restResource.StockTrader.entity.Quote;
import com.restResource.StockTrader.entity.logging.UserCommandLog;
import com.restResource.StockTrader.repository.AccountRepository;
import com.restResource.StockTrader.repository.BuyRepository;
import com.restResource.StockTrader.repository.BuyTriggerRepository;
import com.restResource.StockTrader.repository.InvestmentRepository;
import com.restResource.StockTrader.service.BuyTriggerService;
import com.restResource.StockTrader.service.LoggingService;
import com.restResource.StockTrader.service.QuoteService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping(value = "/buyTrigger")
public class BuyTriggerController {

    private QuoteService quoteService;

    private BuyRepository buyRepository;

    private AccountRepository accountRepository;

    private LoggingService loggingService;

    private BuyTriggerRepository buyTriggerRepository;

    private BuyTriggerService buyTriggerService;

    public BuyTriggerController(
            QuoteService quoteService,
            BuyTriggerRepository buyTriggerRepository,
            LoggingService loggingService,
            BuyTriggerService buyTriggerService,
            AccountRepository accountRepository) {

        this.quoteService = quoteService;
        this.buyTriggerRepository = buyTriggerRepository;
        this.loggingService = loggingService;
        this.buyTriggerService = buyTriggerService;
        this.accountRepository = accountRepository;
    }

    @PostMapping(path = "/amount")
    public @ResponseBody
    HttpStatus createTriggerAmount(
            @RequestParam String userId,
            @RequestParam String stockSymbol,
            @RequestParam int stockAmount,
            @RequestParam int transactionNum) {


        if (stockAmount <= 0) {
            throw new IllegalArgumentException(
                    "The amount parameter must be greater than zero.");
        }

        Optional<BuyTrigger> stockBuyTriggerStatus = buyTriggerRepository.findByUserIdAndStockSymbol(userId, stockSymbol);

        if (stockBuyTriggerStatus.isPresent()) {
            Integer cost = stockBuyTriggerStatus.get().getStock_cost();
            if (cost != null){
                if(accountRepository.removeFunds(userId, cost*stockAmount) == 0){
                    return HttpStatus.BAD_REQUEST; //insufficient funds for the transaction.
                }
            }
            stockBuyTriggerStatus.get().setStock_amount(stockAmount + stockBuyTriggerStatus.get().getStock_amount());
            buyTriggerRepository.save(stockBuyTriggerStatus.get());
        } else {
            BuyTrigger buyTrigger = BuyTrigger.builder()
                .user_id(userId)
                .stock_symbol(stockSymbol)
                .stock_amount(stockAmount)
                .timestamp(LocalDateTime.now())
                .build();
            buyTriggerRepository.save(buyTrigger);
        }



        return HttpStatus.OK;
    }

    @PostMapping(path = "/trigger")
    public @ResponseBody
    HttpStatus createTriggerCost(
            @RequestParam String userId,
            @RequestParam String stockSymbol,
            @RequestParam int stockCost,
            @RequestParam int transactionNum) {

        if (stockCost <= 0) {
            throw new IllegalArgumentException(
                    "The amount parameter must be greater than zero.");
        }

        buyTriggerService.start(userId, stockSymbol, stockCost, transactionNum);

        return HttpStatus.ACCEPTED;
    }

    @PostMapping(path = "/cancel")
    public @ResponseBody
    HttpStatus createTriggerCost(
            @RequestParam String userId,
            @RequestParam String stockSymbol,
            @RequestParam int transactionNum) {

        Optional<BuyTrigger> stockBuyTriggerStatus = buyTriggerRepository.findByUserIdAndStockSymbol(userId, stockSymbol);

        if (stockBuyTriggerStatus.isPresent()) {
            buyTriggerRepository.delete(stockBuyTriggerStatus.get());
            return HttpStatus.OK;
        } else {
            return HttpStatus.BAD_REQUEST;
        }

    }
}
