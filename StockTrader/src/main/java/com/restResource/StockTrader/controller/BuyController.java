package com.restResource.StockTrader.controller;

import com.restResource.StockTrader.entity.PendingBuy;
import com.restResource.StockTrader.entity.Quote;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.restResource.StockTrader.repository.BuyRepository;
import com.restResource.StockTrader.service.QuoteService;

import java.time.LocalDateTime;


@RestController
@RequestMapping(value = "/buy")
public class BuyController {

    private QuoteService quoteService;

    private BuyRepository buyRepository;

    public BuyController(QuoteService quoteService, BuyRepository buyRepository) {
        this.buyRepository = buyRepository;
        this.quoteService = quoteService;
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

        PendingBuy pendingBuy = PendingBuy.builder()
                .userId(userId)
                .stockSymbol(stockSymbol)
                .timestamp(quote.getTimestamp())
                .price(quote.getPrice())
                .build();

        buyRepository.save(pendingBuy);

        return quote;
    }

    @PostMapping(path = "/commit")
    public @ResponseBody
    HttpStatus commitBuy(@RequestParam String userId) {
        PendingBuy pendingBuyToCommit =
                buyRepository
                        .findBuyToCommitForUserId(userId)
                        .orElseThrow(() -> new IllegalStateException(
                                "There was nothing to commit."));

        if (pendingBuyToCommit
                .getTimestamp()
                .isBefore(LocalDateTime.now().minusMinutes(1))) {
            throw new IllegalStateException(
                    "The most recent buy has already expired.");
        }

        // TODO: figure out some way to prevent multiple commits using the same buy.
        // Maybe try to delete the PendingBuy and if it succeeds then it is yours.

        // TODO: update StockRepository to reflect the change.
        return HttpStatus.OK;
    }

    @PostMapping(path = "/cancel")
    public @ResponseBody
    int cancelBuy(@RequestParam String userId) {
        return -1;
    }
}
