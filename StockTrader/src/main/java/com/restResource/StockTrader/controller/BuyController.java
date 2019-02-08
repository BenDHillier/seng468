package com.restResource.StockTrader.controller;

import com.restResource.StockTrader.entity.CommandType;
import com.restResource.StockTrader.entity.PendingBuy;
import com.restResource.StockTrader.entity.Quote;
import com.restResource.StockTrader.entity.logging.ErrorEventLog;
import com.restResource.StockTrader.entity.logging.UserCommandLog;
import com.restResource.StockTrader.repository.AccountRepository;
import com.restResource.StockTrader.repository.InvestmentRepository;
import com.restResource.StockTrader.service.LoggingService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.restResource.StockTrader.repository.BuyRepository;
import com.restResource.StockTrader.service.QuoteService;


@RestController
@RequestMapping(value = "/buy")
public class BuyController {

    private QuoteService quoteService;

    private BuyRepository buyRepository;

    private InvestmentRepository investmentRepository;

    private AccountRepository accountRepository;

    private LoggingService loggingService;

    public BuyController(
            QuoteService quoteService,
            BuyRepository buyRepository,
            InvestmentRepository investmentRepository,
            LoggingService loggingService,
            AccountRepository accountRepository) {

        this.buyRepository = buyRepository;
        this.quoteService = quoteService;
        this.investmentRepository = investmentRepository;
        this.accountRepository = accountRepository;
        this.loggingService = loggingService;
    }

    @PostMapping(path = "/create")
    public @ResponseBody
    Quote createBuy(
            @RequestParam String userId,
            @RequestParam String stockSymbol,
            @RequestParam int amount,
            @RequestParam int transactionNum) {

        loggingService.logUserCommand(
                UserCommandLog.builder()
                        .command(CommandType.BUY)
                        .username(userId)
                        .stockSymbol(stockSymbol)
                        .funds(amount)
                        .transactionNum(transactionNum)
                        .build());

        if (amount <= 0) {
            loggingService.logErrorEvent(
                    ErrorEventLog.builder()
                            .command(CommandType.BUY)
                            .username(userId)
                            .stockSymbol(stockSymbol)
                            .funds(amount)
                            .transactionNum(transactionNum)
                            .errorMessage("The amount parameter must be greater than zero")
                            .build());
            throw new IllegalArgumentException(
                    "The amount parameter must be greater than zero.");
        }

        Quote quote = quoteService.getQuote(stockSymbol, userId, transactionNum);

        if (quote.getPrice() > amount) {
            loggingService.logErrorEvent(
                    ErrorEventLog.builder()
                            .command(CommandType.BUY)
                            .username(userId)
                            .transactionNum(transactionNum)
                            .stockSymbol(stockSymbol)
                            .funds(amount)
                            .errorMessage("The amount parameter must be greater than the quote price")
                            .build());
            // TODO: may want to handle this differently.
            throw new IllegalArgumentException("The amount parameter must be greater than the quote price");
        }

        // Removes any excess amount not needed to purchase the maximum number of stocks.
        Integer roundedAmount = quote.getPrice() * (amount / quote.getPrice());

        // Removing more funds then is available will violate the amount >= 0
        // constraint which will throw an exception.
        try {
            Integer updatedEntriesCount = accountRepository.removeFunds(userId, roundedAmount,transactionNum,"TS1");
            if (updatedEntriesCount != 1) {
                loggingService.logErrorEvent(
                        ErrorEventLog.builder()
                                .command(CommandType.BUY)
                                .username(userId)
                                .transactionNum(transactionNum)
                                .stockSymbol(stockSymbol)
                                .funds(amount)
                                .errorMessage("Error removing funds from account. Expected 1 account to be updated but \" + updatedEntriesCount + \" accounts were updated")
                                .build());
                throw new IllegalStateException(
                        "Error removing funds from account. Expected 1 account to be updated but " +
                                updatedEntriesCount + " accounts were updated");
            }
        } catch (Exception e) {
            // TODO: may want to handle this differently.
            //throw new IllegalStateException("You do not have enough funds.");
        }

        PendingBuy pendingBuy = PendingBuy.builder()
                .userId(userId)
                .stockSymbol(stockSymbol)
                .amount(roundedAmount)
                .timestamp(quote.getTimestamp())
                .price(quote.getPrice())
                .build();

        buyRepository.save(pendingBuy);

        return quote;
    }

    @PostMapping(path = "/commit")
    public @ResponseBody
    HttpStatus commitBuy(@RequestParam String userId,
                         @RequestParam int transactionNum) {

        PendingBuy pendingBuy = claimMostRecentPendingBuy(userId,transactionNum);

        int amountToBuy = pendingBuy.getAmount() / pendingBuy.getPrice();

        investmentRepository.insertOrIncrement(userId, pendingBuy.getStockSymbol(), amountToBuy);

        loggingService.logUserCommand(
                UserCommandLog.builder()
                        .command(CommandType.COMMIT_BUY)
                        .username(userId)
                        .transactionNum(transactionNum)
                        .funds(amountToBuy)
                        .build());

        return HttpStatus.OK;
    }

    @PostMapping(path = "/cancel")
    public @ResponseBody
    HttpStatus cancelBuy(@RequestParam String userId,
                         @RequestParam int transactionNum) {


        PendingBuy pendingBuy = claimMostRecentPendingBuy(userId,transactionNum);

        loggingService.logUserCommand(
                UserCommandLog.builder()
                        .command(CommandType.CANCEL_BUY)
                        .username(userId)
                        .transactionNum(transactionNum)
                        .stockSymbol(pendingBuy.getStockSymbol())
                        .funds(pendingBuy.getAmount())
                        .build());

        accountRepository.updateAccountBalance(userId, pendingBuy.getAmount(), transactionNum,"TS1");

        return HttpStatus.OK;
    }

    // TODO: change from exceptions to something else.
    // I think that it'd be best to return a failed http status code with a message.
    private PendingBuy claimMostRecentPendingBuy(String userId,int transactionNum) {
        while (true) {
            PendingBuy pendingBuy =
                    buyRepository
                            .findMostRecentForUserId(userId)
                            .orElseThrow(() -> new IllegalStateException(
                                    "There was no valid buy."));

            if (pendingBuy.isExpired()) {
                // TODO: May want to remove the expired entry from the DB if
                // this is not going to be handled by something else.
                loggingService.logErrorEvent(
                        ErrorEventLog.builder()
                                .command(CommandType.CANCEL_BUY)
                                .username(userId)
                                .transactionNum(transactionNum)
                                .stockSymbol(pendingBuy.getStockSymbol())
                                .funds(pendingBuy.getAmount())
                                .errorMessage("There was no valid buy")
                                .build());
                throw new IllegalStateException(
                        "There was no valid buy.");
            }

            // If delete fails, then the pendingBuy has already been claimed and
            // we need to get the next most recent pendingBuy.
            try {
                buyRepository.deleteById(pendingBuy.getId());
            } catch (Exception e) {
                // TODO: Verify that the error here is caused by CANCEL_BUY
                loggingService.logErrorEvent(
                        ErrorEventLog.builder()
                                .command(CommandType.CANCEL_BUY)
                                .username(userId)
                                .stockSymbol(pendingBuy.getStockSymbol())
                                .transactionNum(transactionNum)
                                .funds(pendingBuy.getAmount())
                                .errorMessage("Exception in buyRepository.deleteById(pendingBuy.getId())")
                                .build());
                continue;
            }
            return pendingBuy;
        }
    }
}
