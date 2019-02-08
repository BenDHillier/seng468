package com.restResource.StockTrader.controller;
import com.restResource.StockTrader.entity.CommandType;
import com.restResource.StockTrader.entity.logging.DebugEventLog;
import com.restResource.StockTrader.entity.logging.ErrorEventLog;
import com.restResource.StockTrader.entity.logging.UserCommandLog;
import com.restResource.StockTrader.repository.AccountRepository;
import com.restResource.StockTrader.service.LoggingService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class AddController {

    private AccountRepository accountRepository;
    private LoggingService loggingService;

    public AddController(AccountRepository accountRepository,
                         LoggingService loggingService) {
        this.accountRepository = accountRepository;
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
                        .funds(amount)
                        .build());
        loggingService.logDebugEvent(
                DebugEventLog.builder()
                        .command(CommandType.ADD)
                        .username(userId)
                        .funds(amount)
                        .debugMessage("Adding funds to user account")
                        .transactionNum(transactionNum)
                        .build());
        try {
            if (amount <= 0) {
                throw new IllegalArgumentException(
                        "The ADD amount parameter must be greater than zero");
            } else {
                accountRepository.updateAccountBalance(userId, amount,transactionNum,"TS1");
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