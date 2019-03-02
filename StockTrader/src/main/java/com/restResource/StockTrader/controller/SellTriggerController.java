package com.restResource.StockTrader.controller;

import com.restResource.StockTrader.entity.CommandType;
import com.restResource.StockTrader.entity.SellTrigger;
import com.restResource.StockTrader.entity.logging.ErrorEventLog;
import com.restResource.StockTrader.entity.logging.UserCommandLog;
import com.restResource.StockTrader.repository.InvestmentRepository;
import com.restResource.StockTrader.entity.TriggerKey;
import com.restResource.StockTrader.repository.SellTriggerRepository;
import com.restResource.StockTrader.service.LoggingService;
import com.restResource.StockTrader.service.SellTriggerService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping(value = "/sellTrigger")
public class SellTriggerController {

    private LoggingService loggingService;

    private SellTriggerRepository sellTriggerRepository;

    private SellTriggerService sellTriggerService;

    private InvestmentRepository investmentRepository;

    public SellTriggerController(
            SellTriggerRepository sellTriggerRepository,
            LoggingService loggingService,
            SellTriggerService sellTriggerService,
            InvestmentRepository investmentRepository) {

        this.sellTriggerRepository = sellTriggerRepository;
        this.loggingService = loggingService;
        this.sellTriggerService = sellTriggerService;
        this.investmentRepository = investmentRepository;
    }

    @PostMapping(path = "/amount")
    public @ResponseBody
    HttpStatus createTriggerAmount(
            @RequestParam String userId,
            @RequestParam String stockSymbol,
            @RequestParam(value = "amount") int stockAmount,
            @RequestParam int transactionNum) {
        try {
            loggingService.logUserCommand(
                    UserCommandLog.builder()
                            .command(CommandType.SET_SELL_AMOUNT)
                            .username(userId)
                            .stockSymbol(stockSymbol)
                            .transactionNum(transactionNum)
                            .build());
            if (stockAmount <= 0) {
                throw new IllegalArgumentException(
                        "The amount parameter must be greater than zero.");
            }

            Optional<SellTrigger> stockSellTriggerStatus = sellTriggerRepository.findByUserIdAndStockSymbol(userId, stockSymbol);

            if ( stockSellTriggerStatus.isPresent()) {
                SellTrigger sellTrigger = stockSellTriggerStatus.get();
                if (sellTrigger.getStockCost() == null) {
                    int updateCount =
                            sellTriggerRepository.incrementAmountBeforeSetCost(userId, stockSymbol, stockAmount);
                    // If updateCount is zero then cost was already set.
                    if (updateCount == 0) {
                        sellTriggerRepository.incrementAmountAfterSetCost(userId, stockSymbol, stockAmount);
                    }
                } else {
                    try {
                        sellTriggerRepository.incrementAmountAfterSetCost(
                                userId, stockSymbol, stockAmount);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                SellTrigger sellTrigger = SellTrigger.builder()
                        .userId(userId)
                        .stockSymbol(stockSymbol)
                        .stockAmount(stockAmount)
                        .timestamp(LocalDateTime.now())
                        .build();
                sellTriggerRepository.save(sellTrigger);
            }
        } catch(Exception e) {
            loggingService.logErrorEvent(
                    ErrorEventLog.builder()
                            .command(CommandType.SET_SELL_AMOUNT)
                            .username(userId)
                            .stockSymbol(stockSymbol)
                            .transactionNum(transactionNum)
                            .errorMessage(e.getMessage())
                            .build());
            return HttpStatus.BAD_REQUEST;
        }
        return HttpStatus.OK;
    }

    @PostMapping(path = "/trigger")
    public @ResponseBody
    HttpStatus createTriggerCost(
            @RequestParam String userId,
            @RequestParam String stockSymbol,
            @RequestParam(value = "amount") int stockCost,
            @RequestParam int transactionNum) {

        try {
            loggingService.logUserCommand(
                    UserCommandLog.builder()
                            .command(CommandType.SET_SELL_TRIGGER)
                            .username(userId)
                            .stockSymbol(stockSymbol)
                            .transactionNum(transactionNum)
                            .build());

        Optional<SellTrigger> sellStockSnapshot = sellTriggerRepository.findByUserIdAndStockSymbol(userId, stockSymbol);

        if (!sellStockSnapshot.isPresent()) {
            throw new Exception("A trigger amount has not been set for the stock symbol: " + stockSymbol + " for the user: " + userId);
        } else if (sellStockSnapshot.get().getStockCost() != null) {
            throw new Exception("A trigger has already been set");
        }

        SellTrigger sellTrigger = sellStockSnapshot.get();

        if (investmentRepository.removeStocks(userId, sellTrigger.getStockAmount() / stockCost, stockSymbol) == 0) {
            throw new Exception("Insufficient Stock for the Transaction - requesting to remove: " + sellTrigger.getStockAmount() / stockCost);
        }

        if (sellTriggerRepository.addCostAmount(userId, stockCost, stockSymbol) == 0) {
            throw new Exception("A trigger amount has not been set for the stock symbol: " + stockSymbol + " for the user: " + userId);
        }

        sellTriggerService.start(userId, stockSymbol, stockCost, transactionNum);

        } catch( Exception e ) {
                loggingService.logErrorEvent(
                        ErrorEventLog.builder()
                                .command(CommandType.SET_SELL_TRIGGER)
                                .username(userId)
                                .stockSymbol(stockSymbol)
                                .transactionNum(transactionNum)
                                .errorMessage(e.getMessage())
                                .build());
                return HttpStatus.NOT_ACCEPTABLE;
        }
        return HttpStatus.ACCEPTED;
    }

    @PostMapping(path = "/cancel")
    public @ResponseBody
    HttpStatus createTriggerCost(
            @RequestParam String userId,
            @RequestParam String stockSymbol,
            @RequestParam int transactionNum) {
        try {
            loggingService.logUserCommand(
                    UserCommandLog.builder()
                            .command(CommandType.CANCEL_SET_SELL)
                            .username(userId)
                            .stockSymbol(stockSymbol)
                            .transactionNum(transactionNum)
                            .build());

            Optional<SellTrigger> stockSellTriggerStatus = sellTriggerRepository.findByUserIdAndStockSymbol(userId, stockSymbol);
            if(!stockSellTriggerStatus.isPresent()) {
                throw new IllegalArgumentException("Sell trigger does not exist");
            }
            // Stocks have only been removed from a users account if the stockCost has been set.
            // TODO: Remove race condition where stockAmount changed before the delete query below executes.
            if (stockSellTriggerStatus.get().getStockCost() != null) {
                investmentRepository.insertOrIncrement(
                        userId,
                        stockSymbol,
                        stockSellTriggerStatus.get().getStockAmount() / stockSellTriggerStatus.get().getStockCost());
            }

            TriggerKey triggerKey = TriggerKey.builder()
                    .userId(userId)
                    .stockSymbol(stockSymbol)
                    .build();
            sellTriggerRepository.deleteById(triggerKey);
        } catch( Exception e ) {
            loggingService.logErrorEvent(
                    ErrorEventLog.builder()
                            .command(CommandType.CANCEL_SET_SELL)
                            .username(userId)
                            .stockSymbol(stockSymbol)
                            .transactionNum(transactionNum)
                            .errorMessage(e.getMessage())
                            .build());
            return HttpStatus.BAD_REQUEST;
        }
        return HttpStatus.OK;
    }
}
