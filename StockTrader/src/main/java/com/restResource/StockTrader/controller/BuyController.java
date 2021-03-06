package com.restResource.StockTrader.controller;

import com.restResource.StockTrader.entity.CommandType;
import com.restResource.StockTrader.entity.PendingBuy;
import com.restResource.StockTrader.entity.Quote;
import com.restResource.StockTrader.entity.logging.ErrorEventLog;
import com.restResource.StockTrader.entity.logging.SystemEventLog;
import com.restResource.StockTrader.entity.logging.UserCommandLog;
import com.restResource.StockTrader.repository.AccountRepository;
import com.restResource.StockTrader.repository.InvestmentRepository;
import com.restResource.StockTrader.service.LoggingService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.restResource.StockTrader.repository.BuyRepository;
import com.restResource.StockTrader.service.QuoteService;

import java.time.LocalDateTime;
import java.util.Optional;


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
    public ResponseEntity<String> createBuy(
            @RequestParam String userId,
            @RequestParam String stockSymbol,
            @RequestParam int amount,
            @RequestParam int transactionNum) {

        loggingService.logUserCommand(CommandType.BUY.toString(), Long.toString(System.currentTimeMillis()),"TS1",Integer.toString(transactionNum),userId,stockSymbol,"NULL",String.format("%.2f",(amount*1.0)/100));
        try {

            //Can't buy nothing or a negative amount
            if (amount <= 0) { throw new IllegalArgumentException("The amount parameter must be greater than zero."); }

            //Don't hit the quote server if the user account doesn't exist
            if( !accountRepository.accountExists(userId) ) throw new IllegalArgumentException("User account \"" + userId + "\" does not exist!");

            //Get the quote
            Optional<Quote> optionalQuote = quoteService.getQuote(stockSymbol, userId, transactionNum);
            if (!optionalQuote.isPresent()) {
                return null;
            }
            Quote quote = optionalQuote.get();

            //User can't afford the stock at this price
            if (quote.getPrice() > amount) { throw new IllegalArgumentException(userId + " can't afford to buy " + amount + " worth of " + quote.getStockSymbol()); }

            // Removes any excess amount not needed to purchase the maximum number of stocks.
            Integer roundedAmount = quote.getPrice() * (amount / quote.getPrice());
            Integer updatedEntriesCount = accountRepository.removeFunds(userId, roundedAmount,transactionNum,"TS1");

            //Account wasn't updated for some reason
            if (updatedEntriesCount != 1) { throw new IllegalStateException(
                        "Error removing funds from account. Expected 1 account to be updated but " +
                                updatedEntriesCount + " accounts were updated");
            }

            PendingBuy pendingBuy = PendingBuy.builder()
                    .userId(userId)
                    .stockSymbol(stockSymbol)
                    .amount(roundedAmount)
                    .timeCreated(LocalDateTime.now())
                    .timestamp(quote.getTimestamp())
                    .price(quote.getPrice())
                    .build();
            buyRepository.save(pendingBuy);

        } catch (Exception e) {
            System.out.println("Exception in BuyController: " + e.getMessage());
//            loggingService.logErrorEvent(
//                    ErrorEventLog.builder()
//                            .command(CommandType.BUY)
//                            .username(userId)
//                            .stockSymbol(stockSymbol)
//                            .funds(amount)
//                            .transactionNum(transactionNum)
//                            .errorMessage(e.getMessage())
//                            .build());
            return new ResponseEntity<>("BUY error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("BUY success", HttpStatus.OK);
    }

    @PostMapping(path = "/commit")
    public @ResponseBody
    ResponseEntity<String> commitBuy(@RequestParam String userId,
                         @RequestParam int transactionNum) {
        try {
            PendingBuy pendingBuy = claimMostRecentPendingBuy(userId,transactionNum, CommandType.COMMIT_BUY);
            int amountToBuy = pendingBuy.getAmount() / pendingBuy.getPrice();
            investmentRepository.insertOrIncrement(userId, pendingBuy.getStockSymbol(), amountToBuy);
        } catch(Exception e) {
            System.out.println("Exception in BuyController: " + e.getMessage());
            loggingService.logUserCommand(CommandType.COMMIT_BUY.toString(), Long.toString(System.currentTimeMillis()),"TS1",Integer.toString(transactionNum),userId,"NULL","NULL","NULL");
//            loggingService.logErrorEvent(
//                    ErrorEventLog.builder()
//                            .command(CommandType.COMMIT_BUY)
//                            .username(userId)
//                            .transactionNum(transactionNum)
//                            .errorMessage(e.getMessage())
//                            .build());
            return new ResponseEntity<>("COMMIT_BUY error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>("COMMIT_BUY success", HttpStatus.OK);
    }

    @PostMapping(path = "/cancel")
    public @ResponseBody
    ResponseEntity<String> cancelBuy(@RequestParam String userId,
                         @RequestParam int transactionNum) {

        try {
            PendingBuy pendingBuy = claimMostRecentPendingBuy(userId,transactionNum, CommandType.CANCEL_BUY);
            accountRepository.updateAccountBalance(userId, pendingBuy.getAmount(), transactionNum,"TS1");
        } catch( Exception e) {
            System.out.println("Exception in BuyController: " + e.getMessage());
            //command was made during an invalid account state, but we still need to log the activity
            loggingService.logUserCommand(CommandType.CANCEL_BUY.toString(), Long.toString(System.currentTimeMillis()),"TS1",Integer.toString(transactionNum),userId,"NULL","NULL","NULL");
//            loggingService.logErrorEvent(
//                    ErrorEventLog.builder()
//                            .command(CommandType.CANCEL_BUY)
//                            .username(userId)
//                            .transactionNum(transactionNum)
//                            .errorMessage(e.getMessage())
//                            .build());
            return new ResponseEntity<>("CANCEL_BUY error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("CANCEL_BUY success", HttpStatus.OK);
    }

    // TODO: change from exceptions to something else.
    // I think that it'd be best to return a failed http status code with a message.
    private PendingBuy claimMostRecentPendingBuy(String userId,int transactionNum, CommandType commandType) {
        while (true) {
            PendingBuy pendingBuy =
                    buyRepository
                            .findMostRecentForUserId(userId)
                            .orElseThrow(() -> new IllegalStateException(
                                    "There was no valid buy prior to this command."));

            if (pendingBuy.isExpired()) {
                // TODO: May want to remove the expired entry from the DB if
                // this is not going to be handled by something else.
                throw new IllegalStateException(
                        "There was no valid buy prior to this command.");
            }

            // If delete fails, then the pendingBuy has already been claimed and
            // we need to get the next most recent pendingBuy.
            try {
                buyRepository.deleteById(pendingBuy.getId());
            } catch (Exception e) {
                System.out.println("Exception in BuyController: " + e.getMessage());
//                loggingService.logErrorEvent(
//                        ErrorEventLog.builder()
//                                .command(CommandType.CANCEL_BUY)
//                                .username(userId)
//                                .stockSymbol(pendingBuy.getStockSymbol())
//                                .transactionNum(transactionNum)
//                                .funds(pendingBuy.getAmount())
//                                .errorMessage("COMMIT_BUY or CANCEL_BUY error: " + e.getMessage())
//                                .build());
                continue;
            }
            //command is allowed (ie there was a valid buy prior to this, etc)
            loggingService.logUserCommand(commandType.toString(), Long.toString(System.currentTimeMillis()),"TS1",Integer.toString(transactionNum),userId,pendingBuy.getStockSymbol(),"NULL",String.format("%.2f",(1.0*pendingBuy.getAmount())/100));
            return pendingBuy;
        }
    }
}
