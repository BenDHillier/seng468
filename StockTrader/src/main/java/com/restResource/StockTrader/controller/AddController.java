package com.restResource.StockTrader.controller;
import com.restResource.StockTrader.bean.UserCommandLogs;
import com.restResource.StockTrader.entity.CommandType;
import com.restResource.StockTrader.entity.logging.ErrorEventLog;
import com.restResource.StockTrader.entity.logging.EventLog;
import com.restResource.StockTrader.entity.logging.UserCommandLog;
import com.restResource.StockTrader.repository.AccountRepository;
//import com.restResource.StockTrader.repository.logging.UserCommandLogRepository;
import com.restResource.StockTrader.service.JaxbMarshallingService;
import com.restResource.StockTrader.service.LoggingService;
import com.restResource.StockTrader.service.UserCommandLogToXMLService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.xml.ws.Response;
import java.io.File;
import java.io.FileInputStream;

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
                                   @RequestParam int amount) {

        loggingService.logUserCommand(CommandType.ADD, userId, null, null, amount);
        loggingService.logDebugEvent(CommandType.ADD,userId,null,null,amount,"Trying to add funds to this guys account");
        try {
            if (amount <= 0) {
                throw new IllegalArgumentException(
                        "The ADD amount parameter must be greater than zero");
            } else {
                accountRepository.updateAccountBalance(userId, amount);
            }
            return HttpStatus.OK;
        } catch (Exception e) {
            System.out.println("Exception in AddController: " + e.toString());
            loggingService.logErrorEvent(CommandType.ADD,userId,null,null,amount,"Amount added must be lteq 0");
            return HttpStatus.BAD_REQUEST;
        }
    }
}