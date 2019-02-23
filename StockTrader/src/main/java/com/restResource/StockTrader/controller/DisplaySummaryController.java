package com.restResource.StockTrader.controller;

import com.restResource.StockTrader.entity.Account;
import com.restResource.StockTrader.entity.AccountTransaction;
import com.restResource.StockTrader.entity.CommandType;
import com.restResource.StockTrader.entity.DisplaySummary;
import com.restResource.StockTrader.entity.logging.UserCommandLog;
import com.restResource.StockTrader.repository.*;
import com.restResource.StockTrader.service.LoggingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
public class DisplaySummaryController {

    private AccountRepository accountRepository;

    private AccountTransactionRepository accountTransactionRepository;

    private InvestmentRepository investmentRepository;

    private BuyTriggerRepository buyTriggerRepository;

    private SellTriggerRepository sellTriggerRepository;

    private LoggingService loggingService;

    public DisplaySummaryController(
            AccountRepository accountRepository,
            AccountTransactionRepository accountTransactionRepository,
            InvestmentRepository investmentRepository,
            BuyTriggerRepository buyTriggerRepository,
            SellTriggerRepository sellTriggerRepository,
            LoggingService loggingService) {

        this.accountRepository = accountRepository;
        this.accountTransactionRepository = accountTransactionRepository;
        this.investmentRepository = investmentRepository;
        this.buyTriggerRepository = buyTriggerRepository;
        this.sellTriggerRepository = sellTriggerRepository;
        this.loggingService = loggingService;
    }

    @GetMapping(path = "/display")
    public ResponseEntity<DisplaySummary> displaySummary(
            @RequestParam String userId,
            @RequestParam  Integer transactionNum) {

        loggingService.logUserCommand(
                UserCommandLog.builder()
                        .command(CommandType.DISPLAY_SUMMARY)
                        .username(userId)
                        .transactionNum(transactionNum)
                        .build());

        return accountRepository.findById(userId).map(account ->
                new ResponseEntity<>(DisplaySummary.builder()
                        .userId(userId)
                        .amount(account.getAmount())
                        .accountTransactions(accountTransactionRepository.findByUsername(userId))
                        .investments(investmentRepository.findByOwner(userId))
                        .buyTriggers(buyTriggerRepository.findByUserId(userId))
                        .sellTriggers(sellTriggerRepository.findByUserId(userId))
                        .build(), HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }
}
