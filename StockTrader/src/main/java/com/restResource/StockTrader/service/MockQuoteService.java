package com.restResource.StockTrader.service;

import com.restResource.StockTrader.entity.CommandType;
import com.restResource.StockTrader.entity.Quote;
import com.restResource.StockTrader.entity.logging.QuoteServerLog;
import com.restResource.StockTrader.entity.logging.SystemEventLog;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Profile("!prod")
public class MockQuoteService implements QuoteService {
    private LoggingService loggingService;
    private Jedis jedis;
    private MockQuoteService(LoggingService loggingService, Jedis jedis) {
        this.loggingService = loggingService;
        this.jedis = jedis;
    }
    @Override
    public Optional<Quote> getQuote(String stockSymbol, String userId, int transactionNum) {
        jedis.hset("user#1", "name", "Peter");
        loggingService.logQuoteServer(
                QuoteServerLog.builder()
                        .price(50)
                        .quoteServerTime(System.currentTimeMillis())
                        .timestamp(System.currentTimeMillis())
                        .transactionNum(transactionNum)
                        .stockSymbol(stockSymbol)
                        .cryptokey("made_up_cryptokey...")
                        .build());


        return Optional.of(Quote.builder()
                .userId(userId)
                .stockSymbol(stockSymbol)
                .price(50)
                .cryptoKey("hd19dg29fj1772nd10")
                .timestamp(LocalDateTime.now())
                .build());
    }
}
