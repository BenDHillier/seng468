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
import org.springframework.web.bind.annotation.*;


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
    public @ResponseBody
    Quote createNewSell(
            @RequestParam String userId,
            @RequestParam String stockSymbol,
            @RequestParam int amount) {
        Quote quote = quoteService.getQuote(stockSymbol, userId);
        try {
            //loggingService.logUserCommand(CommandType.SELL,userId,stockSymbol,null,amount);
            loggingService.logUserCommand(CommandType.SELL,userId,stockSymbol,null,null);

            if (amount <= 0) {
                loggingService.logErrorEvent(CommandType.SELL,userId,stockSymbol,null,amount,"The amount parameter must be greater than zero.");
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
            investmentRepository.removeStocks(userId, stockCount);

            PendingSell pendingSell = PendingSell.builder()
                    .userId(userId)
                    .stockSymbol(stockSymbol)
                    .timestamp(quote.getTimestamp())
                    .stockCount(stockCount)
                    .stockPrice(quote.getPrice())
                    .build();

            sellRepository.save(pendingSell);


        } catch( Exception e ) {
            loggingService.logErrorEvent(CommandType.SELL,userId,null,null,null,"Error in sell controller");
        }
        return quote;
    }

    @PostMapping("/commit")
    public @ResponseBody
    HttpStatus commitSell(@RequestParam String userId) {

        PendingSell pendingSell = claimMostRecentPendingSell(userId);
        loggingService.logUserCommand(CommandType.COMMIT_SELL,userId,pendingSell.getStockSymbol(),null,pendingSell.getStockPrice());

        accountRepository.updateAccountBalance(
                userId,
                pendingSell.getStockPrice() * pendingSell.getStockCount());

        return HttpStatus.OK;
    }

    @PostMapping("/cancel")
    public @ResponseBody
    HttpStatus cancelSell(@RequestParam String userId) {


        PendingSell pendingSell = claimMostRecentPendingSell(userId);
        loggingService.logUserCommand(CommandType.CANCEL_SELL,userId,pendingSell.getStockSymbol(),null,pendingSell.getStockPrice());

        investmentRepository.insertOrIncrement(
                userId,
                pendingSell.getStockSymbol(),
                pendingSell.getStockCount());

        return HttpStatus.OK;
    }

    // TODO: change from exceptions to something else.
    // I think that it'd be best to return a failed http status code with a message.
    private PendingSell claimMostRecentPendingSell(String userId) {
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
                loggingService.logErrorEvent(null,userId,null,null,null,"Error in claimMostRecentPendingSell");
                continue;
            }
            return pendingSell;
        }
    }

}
