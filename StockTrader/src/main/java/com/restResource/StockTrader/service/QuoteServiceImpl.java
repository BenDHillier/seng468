package com.restResource.StockTrader.service;

import java.io.*;
import java.net.*;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableMap;

import java.time.Duration;
import java.time.LocalDateTime;
import com.restResource.StockTrader.entity.Quote;
import com.restResource.StockTrader.entity.converter.LocalDateTimeToEpochConverter;
import com.restResource.StockTrader.entity.logging.QuoteServerLog;
import com.sun.org.apache.xpath.internal.operations.Quo;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.ZoneId;
import java.time.temporal.TemporalUnit;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@Profile("prod")
public class QuoteServiceImpl implements QuoteService {

    private LoggingService loggingService;

    private static Cache<String,Quote> quoteCache = CacheBuilder.newBuilder()
            .expireAfterWrite(50, TimeUnit.SECONDS)
            .maximumSize(1000)
            .initialCapacity(1000)
            .build();

    public QuoteServiceImpl(LoggingService loggingService) {
        this.loggingService = loggingService;
    }

    private Quote getQuoteFromServer(String stockSymbol, String userId, int transactionNum) throws Exception {
        String quoteServerHost = "quoteserve.seng.uvic.ca";
        int quoteServerPort = 4452;

        String response;
        Socket socket = new Socket(quoteServerHost, quoteServerPort);
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out.println(stockSymbol+","+userId);
        response = in.readLine();
        out.close();
        in.close();
        socket.close();
        if (response == null || response.equals("")) {
            throw new IllegalStateException("response not valid");
        }
        String[] responseList = response.split(",");
        // Valid response list should be a length of 5.
        if (responseList.length < 5) {
            throw new IllegalStateException("response not valid");
        }

        int price = extractPriceFromResponseList(responseList);
        Long quoteServerTime = extractQuoteServerTimeFromResponseList(responseList);
        String cryptoKey = responseList[4];

        //Only replacing trailing space on cryptokey
        Quote quote = Quote.builder()
                .stockSymbol(stockSymbol)
                .userId(userId)
                .price(price)
                .timestamp(LocalDateTime.now())
                .cryptoKey(cryptoKey.replaceAll("\\s+$", ""))
                .build();

        loggingService.logQuoteServer(
                QuoteServerLog.builder()
                        .price(responseList[0])
                        .username(userId)
                        .quoteServerTime(quoteServerTime)
                        .timestamp(System.currentTimeMillis())
                        .transactionNum(transactionNum)
                        .stockSymbol(stockSymbol)
                        .cryptokey(quote.getCryptoKey())
                        .build());
        return quote;
    }

    public Optional<Quote> getQuote(String stockSymbol, String userId, int transactionNum) {
        try {
            Quote quote = quoteCache.get(stockSymbol, () -> getQuoteFromServer(stockSymbol,userId,transactionNum));
            return Optional.of(quote);
        } catch (Exception e) {
            return Optional.empty();
        }

    }

    private Integer extractPriceFromResponseList(String[] responseList) {
        return (int) (Double.parseDouble(responseList[0]) * 100);
    }

    private Long extractQuoteServerTimeFromResponseList(String[] responseList) {
        String responseTimestamp = responseList[3];
        return Long.parseLong(responseTimestamp);
    }
}
