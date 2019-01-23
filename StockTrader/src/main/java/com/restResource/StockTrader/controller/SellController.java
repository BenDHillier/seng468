package com.restResource.StockTrader.controller;

import com.restResource.StockTrader.entity.Investment;
import com.restResource.StockTrader.entity.InvestmentId;
import com.restResource.StockTrader.entity.Quote;
import com.restResource.StockTrader.entity.PendingSell;
import com.restResource.StockTrader.repository.AccountRepository;
import com.restResource.StockTrader.repository.InvestmentRepository;
import com.restResource.StockTrader.repository.SellRepository;
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

    public SellController(
            QuoteService quoteService,
            SellRepository sellRepository,
            InvestmentRepository investmentRepository,
            AccountRepository accountRepository) {

        this.quoteService = quoteService;
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

        if (amount <= 0) {
            throw new IllegalArgumentException(
                    "The amount parameter must be greater than zero.");
        }

        Quote quote = quoteService.getQuote(stockSymbol, userId);

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

        return quote;
    }

    @PostMapping("/commit")
    public @ResponseBody
    HttpStatus commitSell(@RequestParam String userId) {
        PendingSell pendingSell = claimMostRecentPendingSell(userId);

        accountRepository.updateAccountBalance(
                userId,
                pendingSell.getStockPrice() * pendingSell.getStockCount());

        return HttpStatus.OK;
    }

    @PostMapping("/cancel")
    public @ResponseBody
    HttpStatus cancelSell(@RequestParam String userId) {
        PendingSell pendingSell = claimMostRecentPendingSell(userId);

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
                continue;
            }
            return pendingSell;
        }
    }

}
