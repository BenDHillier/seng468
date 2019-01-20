package com.restResource.StockTrader.controller;

import com.restResource.StockTrader.entity.BuyEntity;
import com.restResource.StockTrader.entity.Quote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.restResource.StockTrader.repository.BuyRepository;
import com.restResource.StockTrader.service.QuoteService;

import java.time.LocalDateTime;


@RestController
public class BuyController {
    @Autowired
    private QuoteService quoteService;

    @Autowired
    private BuyRepository buyRepository;

    @PostMapping(path = "/buy")
    public @ResponseBody
    Quote createNewBuy(
            @RequestParam String userId,
            @RequestParam String stockSymbol,
            @RequestParam int amount) {

        if (amount <= 0) {
            throw new IllegalArgumentException(
                    "The amount parameter must be greater than zero.");
        }

        // TODO: Confirm user has enough funds and set aside funds.

        Quote quote = quoteService.getQuote(stockSymbol, userId);

        BuyEntity buyEntity = BuyEntity.builder()
                .userId(userId)
                .stockSymbol(stockSymbol)
                .timestamp(quote.getTimestamp())
                .build();

        buyRepository.save(buyEntity);

        // TODO: Add 60 second timer to check that buy was completed.
        return quote;

    }

    @Async
    public void checkBuyStatusForQuoteResend() {

    }
}
