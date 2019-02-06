package com.restResource.StockTrader.controller;

import com.restResource.StockTrader.entity.CommandType;
import com.restResource.StockTrader.entity.Quote;
import com.restResource.StockTrader.entity.logging.ErrorEventLog;
import com.restResource.StockTrader.entity.logging.UserCommandLog;
import com.restResource.StockTrader.service.LoggingService;
import com.restResource.StockTrader.service.QuoteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class QuoteController {
    private QuoteService quoteService;

    private LoggingService loggingService;

    public QuoteController(
            QuoteService quoteService,
            LoggingService loggingService) {

        this.quoteService = quoteService;
        this.loggingService = loggingService;
    }
    //does not account for caching -> needs to be updated
    @GetMapping(value = "/quote")
    public
    ResponseEntity<Quote> getQuotePrice(@RequestParam String stockSymbol,
                                 @RequestParam String userId) {

        loggingService.logUserCommand(CommandType.QUOTE,userId,stockSymbol,null,null);


        try {
            Quote quote = quoteService.getQuote(stockSymbol, userId);
            return new ResponseEntity<>(quote, HttpStatus.OK);
        } catch( IllegalArgumentException e ) {
            loggingService.logErrorEvent(CommandType.QUOTE,userId,stockSymbol,null,null,"Error during quote request");
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
}
