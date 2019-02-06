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

    private LoggingService loggingService;

    private BuyTriggerRepository buyTriggerRepository;

    private BuyTriggerService buyTriggerService;

    public BuyTriggerController(
            QuoteService quoteService,
            BuyTriggerRepository buyTriggerRepository,
            LoggingService loggingService,
            BuyTriggerService buyTriggerService) {

        this.quoteService = quoteService;
        this.buyTriggerRepository = buyTriggerRepository;
        this.loggingService = loggingService;
        this.buyTriggerService = buyTriggerService;
    }

    @PostMapping(path = "/amount")
    public @ResponseBody
    HttpStatus createTriggerAmount(
            @RequestParam String userId,
            @RequestParam String stockSymbol,
            @RequestParam int stockAmount) {


        if (stockAmount <= 0) {
            throw new IllegalArgumentException(
                    "The amount parameter must be greater than zero.");
        }

        BuyTrigger buyTrigger = BuyTrigger.builder()
                .user_id(userId)
                .stock_symbol(stockSymbol)
                .stock_amount(stockAmount)
                .timestamp(LocalDateTime.now())
                .build();
        buyTriggerRepository.save(buyTrigger);

        //buyTriggerRepository.setBuyTriggerAmount(userId, stockSymbol, stockAmount);
        Optional<BuyTrigger> test = buyTriggerRepository.findByUserIdAndStockSymbol(userId, stockSymbol);
        System.out.println(test.toString());
        buyTriggerService.start(userId, stockSymbol);

        return HttpStatus.OK;
    }

    @PostMapping(path = "/cost")
    public @ResponseBody
    HttpStatus createTriggerCost(
            @RequestParam String userId,
            @RequestParam String stockSymbol,
            @RequestParam int stockCost) {

        if (stockCost <= 0) {
            throw new IllegalArgumentException(
                    "The amount parameter must be greater than zero.");
        }

        buyTriggerRepository.setBuyTriggerCost(userId, stockSymbol, stockCost);

        return HttpStatus.OK;
    }
}
