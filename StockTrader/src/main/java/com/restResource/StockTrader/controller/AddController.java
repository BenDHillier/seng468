package com.restResource.StockTrader.controller;
import com.restResource.StockTrader.entity.CommandType;
import com.restResource.StockTrader.entity.logging.DebugEventLog;
import com.restResource.StockTrader.entity.logging.ErrorEventLog;
import com.restResource.StockTrader.entity.logging.UserCommandLog;
import com.restResource.StockTrader.service.AccountService;
import com.restResource.StockTrader.service.LoggingService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class AddController {

    private AccountService accountService;
    private LoggingService loggingService;

    public AddController(AccountService accountService,
                         LoggingService loggingService) {
        this.accountService = accountService;
        this.loggingService = loggingService;
    }

    @PutMapping(value = "/add")
    public @ResponseBody
    HttpStatus addToAccountBalance(@RequestParam String userId,
                                   @RequestParam int amount,
                                   @RequestParam int transactionNum) {

        loggingService.logUserCommand(
                UserCommandLog.builder()
                        .command(CommandType.ADD)
                        .username(userId)
                        .transactionNum(transactionNum)
                        //(1.0*pendingSell.getStockPrice())/100
                        .funds(String.format("%.2f",(amount*1.0)/100))
                        .build());
        try {
            if (amount <= 0) {
                throw new IllegalArgumentException(
                        "The ADD amount parameter must be greater than zero");
            } else {
                accountService.updateAccountBalance(userId, amount,transactionNum);
            }
            return HttpStatus.OK;
        } catch (Exception e) {
            System.out.println("Exception in AddController: " + e.toString());
            loggingService.logErrorEvent(
                    ErrorEventLog.builder()
                            .command(CommandType.ADD)
                            .username(userId)
                            .transactionNum(transactionNum)
                            .funds(amount)
                            .errorMessage("Amount added must be less than or equal to zero")
                            .build());
            return HttpStatus.BAD_REQUEST;
        }
    }
}