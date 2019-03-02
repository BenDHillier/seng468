package com.restResource.StockTrader.controller;

import com.restResource.StockTrader.entity.*;
import com.restResource.StockTrader.entity.logging.ErrorEventLog;
import com.restResource.StockTrader.entity.logging.UserCommandLog;
import com.restResource.StockTrader.repository.AccountRepository;
import com.restResource.StockTrader.repository.InvestmentRepository;
import com.restResource.StockTrader.repository.SellRepository;
import com.restResource.StockTrader.service.LoggingService;
import com.restResource.StockTrader.service.QuoteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@RestController
@RequestMapping(value = "/sell")
public class SellController {

    private QuoteService quoteService;

    private SellRepository sellRepository;

    private InvestmentRepository investmentRepository;

    private AccountRepository accountRepository;

    private LoggingService loggingService;

    public SellController(
            QuoteService quoteService,
            SellRepository sellRepository,
            InvestmentRepository investmentRepository,
            LoggingService loggingService,
            AccountRepository accountRepository) {

        this.quoteService = quoteService;
        this.loggingService = loggingService;
        this.sellRepository = sellRepository;
        this.investmentRepository = investmentRepository;
        this.accountRepository = accountRepository;
    }

    @PostMapping("/create")
    public
    ResponseEntity<String> createNewSell(
            @RequestParam String userId,
            @RequestParam String stockSymbol,
            @RequestParam int amount,
            @RequestParam int transactionNum) {
        Optional<Quote> optionalQuote = quoteService.getQuote(stockSymbol, userId, transactionNum);
        if (!optionalQuote.isPresent()) {
            return null;
        }
        Quote quote = optionalQuote.get();
        try {
            loggingService.logUserCommand(
                    UserCommandLog.builder()
                            .command(CommandType.SELL)
                            .username(userId)
                            .transactionNum(transactionNum)
                            .stockSymbol(stockSymbol)
                            .funds(amount)
                            .build());

            //Don't hit the quote server if the user account doesn't exist
            if( !accountRepository.accountExists(userId) ) throw new IllegalArgumentException("User account \"" + userId + "\" does not exist!");

            //quote = quoteService.getQuote(stockSymbol, userId,transactionNum);

            if (amount <= 0) {
                throw new IllegalArgumentException(
                        "The amount parameter must be greater than zero.");
            }

            Investment investment = investmentRepository.findById(
                    InvestmentId.builder()
                            .owner(userId)
                            .stockSymbol(stockSymbol)
                            .build())
                    .orElseThrow(() -> new IllegalStateException(
                            "No investments owned of specified stock"));
            // If stockCount is higher then number of stocks owned, then just sell all.
            int stockCount = Math.min(
                    amount / quote.getPrice(),
                    investment.getStockCount());

            // Set aside stocks to avoid duplicate sells.
            investmentRepository.removeStocks(userId, stockCount, stockSymbol);

            PendingSell pendingSell = PendingSell.builder()
                    .userId(userId)
                    .stockSymbol(stockSymbol)
                    .timestamp(quote.getTimestamp())
                    .stockCount(stockCount)
                    .stockPrice(quote.getPrice())
                    .build();
            sellRepository.save(pendingSell);

        } catch( Exception e ) {
            loggingService.logErrorEvent(
                    ErrorEventLog.builder()
                            .command(CommandType.SELL)
                            .username(userId)
                            .stockSymbol(stockSymbol)
                            .transactionNum(transactionNum)
                            .funds(amount)
                            .errorMessage(e.getMessage())
                            .build());
            return new ResponseEntity<>("SELL error: " + e.getMessage(), HttpStatus.BAD_REQUEST);

        }
        return new ResponseEntity<>("SELL success", HttpStatus.OK);
    }

    @PostMapping("/commit")
    public @ResponseBody
    ResponseEntity<String> commitSell(@RequestParam String userId,
                          @RequestParam int transactionNum) {

        try {
            PendingSell pendingSell = claimMostRecentPendingSell(userId,transactionNum, CommandType.COMMIT_SELL);
            accountRepository.updateAccountBalance(
                    userId,
                    pendingSell.getStockPrice() * pendingSell.getStockCount(),
                    transactionNum,"TS1");
        } catch( Exception e ) {
            loggingService.logUserCommand(
                    UserCommandLog.builder()
                            .command(CommandType.COMMIT_SELL)
                            .username(userId)
                            .transactionNum(transactionNum)
                            .build());
            loggingService.logErrorEvent(
                    ErrorEventLog.builder()
                            .command(CommandType.COMMIT_SELL)
                            .username(userId)
                            .transactionNum(transactionNum)
                            .errorMessage(e.getMessage())
                            .build());
            return new ResponseEntity<>("COMMIT_SELL error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("COMMIT_SELL success", HttpStatus.OK);
    }

    @PostMapping("/cancel")
    public @ResponseBody
    ResponseEntity<String> cancelSell(@RequestParam String userId,
                          @RequestParam int transactionNum) {

        try {
            PendingSell pendingSell = claimMostRecentPendingSell(userId,transactionNum, CommandType.CANCEL_SELL);
            investmentRepository.insertOrIncrement(
                    userId,
                    pendingSell.getStockSymbol(),
                    pendingSell.getStockCount());
        } catch( Exception e ) {
            //command was made during an invalid account state, but we still need to log the activity
            loggingService.logUserCommand(
                    UserCommandLog.builder()
                            .command(CommandType.CANCEL_SELL)
                            .username(userId)
                            .transactionNum(transactionNum)
                            .build());
            loggingService.logErrorEvent(
                    ErrorEventLog.builder()
                            .command(CommandType.CANCEL_BUY)
                            .username(userId)
                            .transactionNum(transactionNum)
                            .errorMessage(e.getMessage())
                            .build());
            return new ResponseEntity<>("CANCEL_SELL error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("CANCEL_SELL success", HttpStatus.OK);
    }

    // TODO: change from exceptions to something else.
    // I think that it'd be best to return a failed http status code with a message.
    private PendingSell claimMostRecentPendingSell(String userId, int transactionNum, CommandType commandType) {
        while (true) {
            PendingSell pendingSell =
                    sellRepository
                            .findMostRecentForUserId(userId)
                            .orElseThrow(() -> new IllegalStateException(
                                    "There was no valid buy."));
            if (pendingSell.isExpired()) {
                // TODO: May want to remove the expired entry from the DB if
                // this is not going to be handled by something else.
                throw new IllegalStateException(
                        "There was no valid buy.");
            }

            // If delete fails, then the pendingSell has already been claimed and
            // we need to get the next most recent pendingSell.
            try {
                sellRepository.deleteById(pendingSell.getId());
            } catch (Exception e) {
                loggingService.logErrorEvent(
                        ErrorEventLog.builder()
                                .command(CommandType.CANCEL_BUY)
                                .username(userId)
                                .stockSymbol(pendingSell.getStockSymbol())
                                .transactionNum(transactionNum)
                                .funds(pendingSell.getStockPrice())
                                .errorMessage("COMMIT_SELL or CANCEL_SELL error: " + e.getMessage())
                                .build());
                continue;
            }
            //command is allowed (ie there was a valid sell prior to this, etc)
            loggingService.logUserCommand(
                    UserCommandLog.builder()
                            .command(commandType)
                            .username(userId)
                            .transactionNum(transactionNum)
                            .stockSymbol(pendingSell.getStockSymbol())
                            .funds(pendingSell.getStockPrice())
                            .build());
            return pendingSell;
        }
    }

}
