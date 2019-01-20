package com.restResource.StockTrader.controller;

import com.restResource.StockTrader.entity.BuyEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.restResource.StockTrader.repository.BuyRepository;
import com.restResource.StockTrader.service.QuoteService;


@RestController
public class BuyController {
    @Autowired
    private QuoteService quoteService;

    @Autowired
    private BuyRepository buyRepository;

    @PostMapping(path = "/buy")
    public @ResponseBody
    int createNewBuy(
            @RequestParam String userId,
            @RequestParam String stockSymbol,
            @RequestParam int amount) {

        if (amount <= 0) {
            // TODO: send fail response
        }

        BuyEntity buyEntity = BuyEntity.builder()
                .userId(userId)
                .stockSymbol(stockSymbol)
                .build();

        buyRepository.save(buyEntity);

        int quote = quoteService.getQuote(stockSymbol, userId);
        // TODO: Add 60 second timer to check that buy was completed.
        return quote;

    }
}
