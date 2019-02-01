package com.restResource.StockTrader.controller;

import com.restResource.StockTrader.entity.Quote;
import com.restResource.StockTrader.service.QuoteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class QuoteController {
    private QuoteService quoteService;

    public QuoteController(
            QuoteService quoteService) {

        this.quoteService = quoteService;
    }
    //does not account for caching -> needs to be updated
    @GetMapping(value = "/quote")
    public
    ResponseEntity<Quote> getQuotePrice(@RequestParam String stockSymbol,
                                 @RequestParam String userId) {
        try {
            Quote quote = quoteService.getQuote(stockSymbol, userId);
            return new ResponseEntity<>(quote, HttpStatus.OK);
        } catch( IllegalArgumentException e ) {
            System.out.println("Exception in QuoteController: " + e.toString());
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
}