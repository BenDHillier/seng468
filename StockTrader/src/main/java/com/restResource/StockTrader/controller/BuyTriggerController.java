package com.restResource.StockTrader.controller;

import com.restResource.StockTrader.entity.BuyTrigger;
import com.restResource.StockTrader.entity.CommandType;
import com.restResource.StockTrader.entity.logging.ErrorEventLog;
import com.restResource.StockTrader.entity.logging.UserCommandLog;
import com.restResource.StockTrader.repository.AccountRepository;
import com.restResource.StockTrader.repository.BuyTriggerRepository;
import com.restResource.StockTrader.service.BuyTriggerService;
import com.restResource.StockTrader.service.LoggingService;
import com.restResource.StockTrader.service.QuoteService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping(value = "/buyTrigger")
public class BuyTriggerController {

    private AccountRepository accountRepository;

    private LoggingService loggingService;

    private BuyTriggerRepository buyTriggerRepository;

    private BuyTriggerService buyTriggerService;

    public BuyTriggerController(
            BuyTriggerRepository buyTriggerRepository,
            LoggingService loggingService,
            BuyTriggerService buyTriggerService,
            AccountRepository accountRepository) {

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


        loggingService.logUserCommand(
                UserCommandLog.builder()
                        .command(CommandType.SET_BUY_AMOUNT)
                        .username(userId)
                        .stockSymbol(stockSymbol)
                        .transactionNum(transactionNum)
                        .build());

        if (stockAmount <= 0) {
            loggingService.logErrorEvent(
                    ErrorEventLog.builder()
                            .command(CommandType.SET_BUY_AMOUNT)
                            .username(userId)
                            .stockSymbol(stockSymbol)
                            .transactionNum(transactionNum)
                            .errorMessage("The amount parameter must be greater than zero")
                            .build());
            //FIXME do we want to throw the error or send a HttpStatus message?
            throw new IllegalArgumentException(
                    "The amount parameter must be greater than zero.");
        }

        Optional<BuyTrigger> stockBuyTriggerStatus = buyTriggerRepository.findByUserIdAndStockSymbol(userId, stockSymbol);

        if (stockBuyTriggerStatus.isPresent()) {
            Integer cost = stockBuyTriggerStatus.get().getStock_cost();
            if (cost != null) {
                if (accountRepository.removeFunds(userId, cost*stockAmount, transactionNum, "TS1") == 0) {
                    loggingService.logErrorEvent(
                            ErrorEventLog.builder()
                                    .command(CommandType.SET_BUY_AMOUNT)
                                    .username(userId)
                                    .stockSymbol(stockSymbol)
                                    .transactionNum(transactionNum)
                                    .errorMessage("Insufficient Funds for the Transaction - requesting to remove: " + cost)
                                    .build());
                    return HttpStatus.BAD_REQUEST; //insufficient funds for the transaction.
                }
            }
            stockBuyTriggerStatus.get().setStock_amount(stockAmount + stockBuyTriggerStatus.get().getStock_amount());
            buyTriggerRepository.save(stockBuyTriggerStatus.get());
        } else {
            BuyTrigger buyTrigger = BuyTrigger.builder()
                .user_id(userId)
                .stock_amount(stockAmount)
                .stock_symbol(stockSymbol)
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

        loggingService.logUserCommand(
                UserCommandLog.builder()
                        .command(CommandType.SET_BUY_TRIGGER)
                        .username(userId)
                        .stockSymbol(stockSymbol)
                        .transactionNum(transactionNum)
                        .build());

        buyTriggerService.start(userId, stockSymbol, stockCost, transactionNum);

        return HttpStatus.ACCEPTED; //we do accepted since we cant be sure it worked but we can be sure we passed it to a thread
    }

    @PostMapping(path = "/cancel")
    public @ResponseBody
    HttpStatus createTriggerCost(
            @RequestParam String userId,
            @RequestParam String stockSymbol,
            @RequestParam int transactionNum) {

        loggingService.logUserCommand(
                UserCommandLog.builder()
                        .command(CommandType.CANCEL_BUY_TRIGGER)
                        .username(userId)
                        .stockSymbol(stockSymbol)
                        .transactionNum(transactionNum)
                        .build());

        Optional<BuyTrigger> stockBuyTriggerStatus = buyTriggerRepository.findByUserIdAndStockSymbol(userId, stockSymbol);

        if (stockBuyTriggerStatus.isPresent()) {
            //refund the money
            //TODO add a check here to make sure it worked
            accountRepository.updateAccountBalance(userId, stockBuyTriggerStatus.get().getStock_amount()*stockBuyTriggerStatus.get().getStock_cost(),transactionNum, "TS1");
            buyTriggerRepository.delete(stockBuyTriggerStatus.get());
            return HttpStatus.OK;
        } else {
            loggingService.logErrorEvent(
                    ErrorEventLog.builder()
                            .command(CommandType.CANCEL_BUY_TRIGGER)
                            .username(userId)
                            .stockSymbol(stockSymbol)
                            .transactionNum(transactionNum)
                            .errorMessage("The amount parameter must be greater than zero")
                            .build());
            return HttpStatus.BAD_REQUEST;
        }

    }
}
