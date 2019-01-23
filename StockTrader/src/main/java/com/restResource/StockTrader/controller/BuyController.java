package com.restResource.StockTrader.controller;

import com.restResource.StockTrader.entity.PendingBuy;
import com.restResource.StockTrader.entity.Quote;
import com.restResource.StockTrader.repository.InvestmentRepository;
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

    public BuyController(QuoteService quoteService, BuyRepository buyRepository, InvestmentRepository investmentRepository) {
        this.buyRepository = buyRepository;
        this.quoteService = quoteService;
        this.investmentRepository = investmentRepository;
    }

    @PostMapping(path = "/create")
    public @ResponseBody
    Quote createBuy(
            @RequestParam String userId,
            @RequestParam String stockSymbol,
            @RequestParam int amount) {

        if (amount <= 0) {
            throw new IllegalArgumentException(
                    "The amount parameter must be greater than zero.");
        }

        // TODO: Confirm user has enough funds and set aside funds.

        Quote quote = quoteService.getQuote(stockSymbol, userId);

        if (quote.getPrice() > amount) {
            // TODO: may want to handle this differently.
            throw new IllegalArgumentException("The amount parameter must be greater than the quote price");
        }

        PendingBuy pendingBuy = PendingBuy.builder()
                .userId(userId)
                .stockSymbol(stockSymbol)
                .amount(amount)
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

        int remainingFundsFromBuy =
                pendingBuy.getAmount() - (pendingBuy.getPrice() * amountToBuy);
        // TODO: add remainingFundsFromBuy back to users account. This is not
        // necessary if the amount set aside is rounded down in createBuy.

        return HttpStatus.OK;
    }

    @PostMapping(path = "/cancel")
    public @ResponseBody
    HttpStatus cancelBuy(@RequestParam String userId) {
        claimMostRecentPendingBuy(userId);

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
                throw new IllegalStateException(
                        "There was no valid buy.");
            }

            // If delete fails, then the pendingBuy has already been claimed and
            // we need to get the next most recent pendingBuy.
            try {
                buyRepository.deleteById(pendingBuy.getId());
            } catch (Exception e) {
                continue;
            }
            return pendingBuy;
        }
    }
}
