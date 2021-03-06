package com.restResource.StockTrader.controller;

import com.restResource.StockTrader.entity.CommandType;
import com.restResource.StockTrader.entity.Quote;
import com.restResource.StockTrader.entity.logging.ErrorEventLog;
import com.restResource.StockTrader.service.LoggingService;
import com.restResource.StockTrader.service.QuoteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

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
                                        @RequestParam String userId,
                                        @RequestParam int transactionNum) {
        loggingService.logUserCommand(CommandType.QUOTE.toString(), Long.toString(System.currentTimeMillis()),"TS1",Integer.toString(transactionNum),userId,stockSymbol,"NULL","NULL");

        try {
            Optional<Quote> optionalQuote = quoteService.getQuote(stockSymbol, userId, transactionNum);
            if (!optionalQuote.isPresent()) {
                throw new Exception("QuoteController: Quote was null");
            }
            Quote quote = optionalQuote.get();
            return new ResponseEntity<>(quote, HttpStatus.OK);
        } catch( Exception e ) {
            System.out.println("Exception in QuoteController: " + e.getMessage());
//            loggingService.logErrorEvent(
//                    ErrorEventLog.builder()
//                            .command(CommandType.QUOTE)
//                            .username(userId)
//                            .transactionNum(transactionNum)
//                            .errorMessage("Error during quote request")
//                            .build());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
