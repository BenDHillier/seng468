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
            @RequestParam int amount) {

        loggingService.logUserCommand(CommandType.BUY, userId,stockSymbol,null,amount);

        if (amount <= 0) {
            loggingService.logErrorEvent(CommandType.BUY,userId, stockSymbol,null,amount,"The amount parameter must be greater than zero.");
            throw new IllegalArgumentException(
                    "The amount parameter must be greater than zero.");
        }

        Quote quote = quoteService.getQuote(stockSymbol, userId);

        if (quote.getPrice() > amount) {
            loggingService.logErrorEvent(CommandType.BUY,userId, stockSymbol,null,amount,"The amount parameter must be greater than the quote price");
            // TODO: may want to handle this differently.
            throw new IllegalArgumentException("The amount parameter must be greater than the quote price");
        }

        // Removes any excess amount not needed to purchase the maximum number of stocks.
        Integer roundedAmount = quote.getPrice() * (amount / quote.getPrice());

        // Removing more funds then is available will violate the amount >= 0
        // constraint which will throw an exception.
        try {
            Integer updatedEntriesCount = accountRepository.removeFunds(userId, roundedAmount);
            if (updatedEntriesCount != 1) {
                loggingService.logErrorEvent(CommandType.BUY,userId, stockSymbol,null,amount,"Error removing funds from account. Expected 1 account to be updated but " + updatedEntriesCount + " accounts were updated");
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
    HttpStatus commitBuy(@RequestParam String userId) {

        PendingBuy pendingBuy = claimMostRecentPendingBuy(userId);

        int amountToBuy = pendingBuy.getAmount() / pendingBuy.getPrice();

        investmentRepository.insertOrIncrement(userId, pendingBuy.getStockSymbol(), amountToBuy);

        loggingService.logUserCommand(CommandType.COMMIT_BUY,userId, pendingBuy.getStockSymbol(),null,pendingBuy.getAmount());

        return HttpStatus.OK;
    }

    @PostMapping(path = "/cancel")
    public @ResponseBody
    HttpStatus cancelBuy(@RequestParam String userId) {


        PendingBuy pendingBuy = claimMostRecentPendingBuy(userId);

        loggingService.logUserCommand(CommandType.CANCEL_BUY,userId, pendingBuy.getStockSymbol(),null,pendingBuy.getAmount());

        accountRepository.updateAccountBalance(userId, pendingBuy.getAmount());

        return HttpStatus.OK;
    }

    // TODO: change from exceptions to something else.
    // I think that it'd be best to return a failed http status code with a message.
    private PendingBuy claimMostRecentPendingBuy(String userId) {
        while (true) {
            PendingBuy pendingBuy =
                    buyRepository
                            .findMostRecentForUserId(userId)
                            .orElseThrow(() -> new IllegalStateException(
                                    "There was no valid buy."));

            if (pendingBuy.isExpired()) {
                // TODO: May want to remove the expired entry from the DB if
                // this is not going to be handled by something else.
                loggingService.logErrorEvent(CommandType.CANCEL_BUY,userId,pendingBuy.getStockSymbol(),null,pendingBuy.getAmount(),"There was no valid buy.");
                throw new IllegalStateException(
                        "There was no valid buy.");
            }

            // If delete fails, then the pendingBuy has already been claimed and
            // we need to get the next most recent pendingBuy.
            try {
                buyRepository.deleteById(pendingBuy.getId());
            } catch (Exception e) {
                // TODO: Verify that the error here is caused by CANCEL_BUY
                loggingService.logErrorEvent(CommandType.CANCEL_BUY,userId,pendingBuy.getStockSymbol(),null,pendingBuy.getAmount(),"Exception in buyRepository.deleteById(pendingBuy.getId())");
                continue;
            }
            return pendingBuy;
        }
    }
}
