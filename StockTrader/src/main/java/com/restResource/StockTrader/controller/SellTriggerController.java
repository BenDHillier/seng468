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
            @RequestParam int stockAmount,
            @RequestParam int transactionNum) {
        try {
//            loggingService.logUserCommand(
//                    UserCommandLog.builder()
//                            .command(CommandType.SET_SELL_AMOUNT)
//                            .username(userId)
//                            .stockSymbol(stockSymbol)
//                            .transactionNum(transactionNum)
//                            .build());
            if (stockAmount <= 0) {
                throw new IllegalArgumentException(
                        "The amount parameter must be greater than zero.");
            }

            //we check to see if we have sufficient stock to sell. We dont care about how much we are selling it for though.
            if (investmentRepository.removeStocks(userId, stockAmount) == 0) { //TODO logging?
                throw new IllegalArgumentException("Insufficient Stock for the Transaction - requesting to remove: ");
            }

            Optional<SellTrigger> stockSellTriggerStatus = sellTriggerRepository.findByUserIdAndStockSymbol(userId, stockSymbol);

            if ( stockSellTriggerStatus.isPresent()) { //FIXME do an insert or increment here instead of getting the trigger
                sellTriggerRepository.incrementStockAmount(userId, stockAmount);
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
            @RequestParam int stockCost,
            @RequestParam int transactionNum) {

        try {
//            loggingService.logUserCommand(
//                    UserCommandLog.builder()
//                            .command(CommandType.SET_SELL_TRIGGER)
//                            .username(userId)
//                            .stockSymbol(stockSymbol)
//                            .transactionNum(transactionNum)
//                            .build());

            if (stockCost <= 0) {
                throw new IllegalArgumentException(
                        "The amount parameter must be greater than zero.");
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
        return HttpStatus.ACCEPTED; //we do accepted since we cant be sure it worked but we can be sure we passed it to a thread
    }

    @PostMapping(path = "/cancel")
    public @ResponseBody
    HttpStatus createTriggerCost(
            @RequestParam String userId,
            @RequestParam String stockSymbol,
            @RequestParam int transactionNum) {
        try {
            //log regardless of outcome
//            loggingService.logUserCommand(
//                    UserCommandLog.builder()
//                            .command(CommandType.CANCEL_SET_SELL)
//                            .username(userId)
//                            .stockSymbol(stockSymbol)
//                            .transactionNum(transactionNum)
//                            .build());

            Optional<SellTrigger> stockSellTriggerStatus = sellTriggerRepository.findByUserIdAndStockSymbol(userId, stockSymbol);
            if(!stockSellTriggerStatus.isPresent()) {
                throw new IllegalArgumentException("Sell trigger does not exist");
            }

            investmentRepository.insertOrIncrement(userId, stockSymbol, stockSellTriggerStatus.get().getStockAmount()); //TODO logging?
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
