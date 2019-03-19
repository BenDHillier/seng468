package com.restResource.StockTrader.controller;

import com.restResource.StockTrader.entity.BuyTrigger;
import com.restResource.StockTrader.entity.CommandType;
import com.restResource.StockTrader.entity.TriggerKey;
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
            @RequestParam(value = "amount") int stockAmount,
            @RequestParam int transactionNum) {

        try {
            loggingService.logUserCommand(CommandType.SET_BUY_AMOUNT.toString(), Long.toString(System.currentTimeMillis()),"TS1",Integer.toString(transactionNum),userId,stockSymbol,"NULL","NULL");

            if (stockAmount <= 0) {
                //invalid request parameter
                throw new IllegalArgumentException("The amount parameter must be greater than zero");
            }
            //TODO remove the find by and replace it with a create or incremement function
            Optional<BuyTrigger> stockBuyTriggerStatus = buyTriggerRepository.findByUserIdAndStockSymbol(userId, stockSymbol);

            if (accountRepository.removeFunds(userId, stockAmount, transactionNum, "TS1") == 0) {
                //insufficient funds for the transaction.
                throw new IllegalArgumentException("Insufficient Funds for the Transaction - requesting to remove: " + stockAmount);
            }

            if (stockBuyTriggerStatus.isPresent()) {
                buyTriggerRepository.incrementStockAmount(userId, stockAmount,stockSymbol);
            } else {
                BuyTrigger buyTrigger = BuyTrigger.builder()
                        .userId(userId)
                        .stockAmount(stockAmount)
                        .stockSymbol(stockSymbol)
                        .timestamp(LocalDateTime.now())
                        .build();
                buyTriggerRepository.save(buyTrigger);
            }
        } catch(Exception e) {
            System.out.println("Exception in BuyTriggerController: " + e.getMessage());
//            loggingService.logErrorEvent(
//                    ErrorEventLog.builder()
//                            .command(CommandType.SET_BUY_AMOUNT)
//                            .username(userId)
//                            .stockSymbol(stockSymbol)
//                            .transactionNum(transactionNum)
//                            .errorMessage(e.getMessage())
//                            .build());
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
//            loggingService.logUserCommand(
//                    UserCommandLog.builder()
//                            .command(CommandType.SET_BUY_TRIGGER)
//                            .username(userId)
//                            .stockSymbol(stockSymbol)
//                            .transactionNum(transactionNum)
//                            .build());

            Optional<BuyTrigger> buyStockSnapshot = buyTriggerRepository.findByUserIdAndStockSymbol(userId, stockSymbol);

            if (!buyStockSnapshot.isPresent()) {
                throw new Exception("A trigger amount has not been set for the stock symbol: " + stockSymbol + " for the user: " + userId);
            } else if (buyStockSnapshot.get().getStockCost() != null) { //we already have a working buy trigger
                throw new Exception("A trigger has already been set");
            } else {
                if (buyTriggerRepository.addCostAmount(userId, stockCost, stockSymbol) == 0) {
                    throw new Exception("A trigger cost is already set for the stock symbol: " + stockSymbol + " for the user: " + userId);
                }
            }
            buyTriggerService.start(userId, stockSymbol, stockCost, transactionNum);
        } catch( Exception e ) {
            System.out.println("Exception in BuyTrigger: " + e.getMessage());
//            loggingService.logErrorEvent(
//                    ErrorEventLog.builder()
//                            .command(CommandType.SET_BUY_TRIGGER)
//                            .username(userId)
//                            .stockSymbol(stockSymbol)
//                            .transactionNum(transactionNum)
//                            .errorMessage(e.getMessage())
//                            .build());
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
//            loggingService.logUserCommand(
//                    UserCommandLog.builder()
//                            .command(CommandType.CANCEL_SET_BUY)
//                            .username(userId)
//                            .stockSymbol(stockSymbol)
//                            .transactionNum(transactionNum)
//                            .build());

            Optional<BuyTrigger> stockBuyTriggerStatus = buyTriggerRepository.findByUserIdAndStockSymbol(userId, stockSymbol);

            if( !stockBuyTriggerStatus.isPresent() ) {
                throw new IllegalArgumentException("Buy trigger does not exist. user: "+userId + " StockSymbol: "+stockSymbol);
            }

            //refund the money
            //TODO add a check here to make sure it worked
            accountRepository.updateAccountBalance(userId, stockBuyTriggerStatus.get().getStockAmount(),transactionNum, "TS1");
            TriggerKey triggerKey = TriggerKey.builder()
                    .userId(userId)
                    .stockSymbol(stockSymbol)
                    .build();
            buyTriggerRepository.deleteById(triggerKey);
        } catch( Exception e ) {
            System.out.println("Exception in BuyTriggerController: " + e.getMessage());
//            loggingService.logErrorEvent(
//                    ErrorEventLog.builder()
//                            .command(CommandType.CANCEL_SET_BUY)
//                            .username(userId)
//                            .stockSymbol(stockSymbol)
//                            .transactionNum(transactionNum)
//                            .errorMessage(e.getMessage())
//                            .build());
            return HttpStatus.BAD_REQUEST;
        }
        return HttpStatus.OK;
    }
}
