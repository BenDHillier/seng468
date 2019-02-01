package com.restResource.StockTrader.controller;
import com.restResource.StockTrader.bean.UserCommandLogs;
import com.restResource.StockTrader.entity.CommandType;
import com.restResource.StockTrader.entity.logging.UserCommandLog;
import com.restResource.StockTrader.repository.AccountRepository;
import com.restResource.StockTrader.service.LoggingService;
import com.restResource.StockTrader.service.UserCommandLogToXMLService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
public class AddController {

    private AccountRepository accountRepository;

    private LoggingService loggingService;

    private UserCommandLogToXMLService userCommandLogToXMLService;

    public AddController(AccountRepository accountRepository, LoggingService loggingService, UserCommandLogToXMLService userCommandLogToXMLService) {
        this.accountRepository = accountRepository;
        this.loggingService = loggingService;
        this.userCommandLogToXMLService = userCommandLogToXMLService;
    }

    @PutMapping(value = "/add")
    public @ResponseBody
    HttpStatus addToAccountBalance(@RequestParam String userId,
                                   @RequestParam int amount) {

        loggingService.logUserCommand(
                UserCommandLog.builder()
                        .username(userId)
                        .funds(amount)
                        .command(CommandType.ADD)
                        .build());

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

    @RequestMapping(value = "/dumplog", produces=MediaType.APPLICATION_XML_VALUE)
    public @ResponseBody
    UserCommandLogs findUserCommandLogs() {
        return userCommandLogToXMLService.findAll();
    }
}