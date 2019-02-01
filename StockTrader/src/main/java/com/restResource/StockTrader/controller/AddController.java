package com.restResource.StockTrader.controller;
import com.restResource.StockTrader.entity.logging.UserCommandLog;
import com.restResource.StockTrader.repository.AccountRepository;
import com.restResource.StockTrader.service.LoggingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
public class AddController {

    private AccountRepository accountRepository;

    private LoggingService loggingService;

    public AddController(AccountRepository accountRepository, LoggingService loggingService) {
        this.accountRepository = accountRepository;
        this.loggingService = loggingService;
    }

    @PutMapping(value = "/add")
    public @ResponseBody
    HttpStatus addToAccountBalance(@RequestParam String userId,
                                   @RequestParam int amount) {

        loggingService.logUserCommand(UserCommandLog.builder().username(userId).funds(amount).command("ADD").build());

        try {
            if( amount <= 0 ) {
                throw new IllegalArgumentException(
                        "The ADD amount parameter must be greater than zero");
            }
            else {
                accountRepository.updateAccountBalance(userId, amount);
            }
            return HttpStatus.OK;
        } catch( IllegalArgumentException e ) {
            System.out.println("Exception in AddController: " + e.toString());
            return HttpStatus.BAD_REQUEST;
        }
    }
}