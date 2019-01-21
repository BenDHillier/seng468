package com.restResource.StockTrader.controller;

import com.restResource.StockTrader.entity.Quote;
import com.restResource.StockTrader.entity.PendingSell;
import com.restResource.StockTrader.repository.SellRepository;
import com.restResource.StockTrader.service.QuoteService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class SellController {

    private QuoteService quoteService;

    private SellRepository sellRepository;

    public SellController(QuoteService quoteService, SellRepository sellRepository) {
        this.quoteService = quoteService;
        this.sellRepository = sellRepository;
    }

    @PostMapping("/sell")
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

        // TODO: Confirm user has the number of stocks necessary and set aside the stocks.

        PendingSell pendingSell = PendingSell.builder()
                .userId(userId)
                .stockSymbol(stockSymbol)
                .timestamp(quote.getTimestamp())
                .price(quote.getPrice())
                .build();

        sellRepository.save(pendingSell);

        return quote;
    }
}
