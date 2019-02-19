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


        loggingService.logUserCommand(
                UserCommandLog.builder()
                        .command(CommandType.SET_SELL_AMOUNT)
                        .username(userId)
                        .stockSymbol(stockSymbol)
                        .transactionNum(transactionNum)
                        .build());

        if (stockAmount <= 0) {
            loggingService.logErrorEvent(
                    ErrorEventLog.builder()
                            .command(CommandType.SET_SELL_AMOUNT)
                            .username(userId)
                            .stockSymbol(stockSymbol)
                            .transactionNum(transactionNum)
                            .errorMessage("The amount parameter must be greater than zero")
                            .build());
            //FIXME do we want to throw the error or send a HttpStatus message?
            throw new IllegalArgumentException(
                    "The amount parameter must be greater than zero.");
        }


        Optional<SellTrigger> stockSellTriggerStatus = sellTriggerRepository.findByUserIdAndStockSymbol(userId, stockSymbol);

        if (stockSellTriggerStatus.isPresent()) { //FIXME do an insert or increment here instead of getting the trigger
            sellTriggerRepository.incrementStockAmount(userId, stockAmount, stockSymbol);
        } else {
            SellTrigger sellTrigger = SellTrigger.builder()
                    .userId(userId)
                    .stockSymbol(stockSymbol)
                    .stockAmount(stockAmount)
                    .timestamp(LocalDateTime.now())
                    .build();
            sellTriggerRepository.save(sellTrigger);
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

        if (stockCost <= 0) {
            throw new IllegalArgumentException(
                    "The amount parameter must be greater than zero.");
        }

        loggingService.logUserCommand(
                UserCommandLog.builder()
                        .command(CommandType.SET_SELL_TRIGGER)
                        .username(userId)
                        .stockSymbol(stockSymbol)
                        .transactionNum(transactionNum)
                        .build());

        Optional<SellTrigger> sellStockSnapshot = sellTriggerRepository.findByUserIdAndStockSymbol(userId, stockSymbol);

        if (!sellStockSnapshot.isPresent()) {
            loggingService.logErrorEvent(
                    ErrorEventLog.builder()
                            .command(CommandType.SET_SELL_TRIGGER)
                            .username(userId)
                            .stockSymbol(stockSymbol)
                            .transactionNum(transactionNum)
                            .errorMessage("A trigger amount has not been set for the stock symbol: " + stockSymbol + " for the user: " + userId)
                            .build());
            return HttpStatus.BAD_REQUEST;
        } else if (sellStockSnapshot.get().getStockCost() != null) {
            return HttpStatus.BAD_REQUEST;
        }

        SellTrigger sellTrigger = sellStockSnapshot.get();

        if (investmentRepository.removeStocks(userId, sellTrigger.getStockAmount() / stockCost) == 0) {
            loggingService.logErrorEvent(
                    ErrorEventLog.builder()
                            .command(CommandType.SET_SELL_AMOUNT)
                            .username(userId)
                            .stockSymbol(stockSymbol)
                            .transactionNum(transactionNum)
                            .errorMessage(
                                    "Insufficient Stock for the Transaction - requesting to remove: " + sellTrigger.getStockAmount() / stockCost)
                            .build());
            return HttpStatus.BAD_REQUEST;

        }

        if (sellTriggerRepository.addCostAmount(userId, stockCost, stockSymbol) == 0) {
            loggingService.logErrorEvent(
                    ErrorEventLog.builder()
                            .command(CommandType.SET_SELL_TRIGGER)
                            .username(userId)
                            .stockSymbol(stockSymbol)
                            .transactionNum(transactionNum)
                            .errorMessage("A trigger amount has not been set for the stock symbol: " + stockSymbol + " for the user: " + userId)
                            .build());
            return HttpStatus.BAD_REQUEST;
        }

        sellTriggerService.start(userId, stockSymbol, stockCost, transactionNum);

        return HttpStatus.ACCEPTED;
    }

    @PostMapping(path = "/cancel")
    public @ResponseBody
    HttpStatus createTriggerCost(
            @RequestParam String userId,
            @RequestParam String stockSymbol,
            @RequestParam int transactionNum) {

        loggingService.logUserCommand(
                UserCommandLog.builder()
                        .command(CommandType.CANCEL_SELL_TRIGGER)
                        .username(userId)
                        .stockSymbol(stockSymbol)
                        .transactionNum(transactionNum)
                        .build());

        Optional<SellTrigger> stockSellTriggerStatus = sellTriggerRepository.findByUserIdAndStockSymbol(userId, stockSymbol);

        if (stockSellTriggerStatus.isPresent()) {
            //refund the stocks
            //TODO add a check here to make sure it worked
            investmentRepository.insertOrIncrement(userId, stockSymbol, stockSellTriggerStatus.get().getStockAmount()); //TODO logging?
            TriggerKey triggerKey = TriggerKey.builder()
                    .userId(userId)
                    .stockSymbol(stockSymbol)
                    .build();
            sellTriggerRepository.deleteById(triggerKey);
            return HttpStatus.OK;
        } else {
            loggingService.logErrorEvent(
                    ErrorEventLog.builder()
                            .command(CommandType.CANCEL_BUY_TRIGGER)
                            .username(userId)
                            .stockSymbol(stockSymbol)
                            .transactionNum(transactionNum)
                            .errorMessage("Sell Trigger does not exist")
                            .build());
            return HttpStatus.BAD_REQUEST;
        }

    }
}
