package com.restResource.StockTrader.service;

import com.google.common.collect.ImmutableMap;
import java.time.LocalDateTime;
import com.restResource.StockTrader.entity.Quote;
import com.restResource.StockTrader.entity.converter.LocalDateTimeToEpochConverter;
import com.restResource.StockTrader.entity.logging.QuoteServerLog;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.ZoneId;
import java.util.Map;
import java.util.Optional;

@Service
@Profile("prod")
public class QuoteServiceImpl implements QuoteService {

    private LoggingService loggingService;

    public QuoteServiceImpl(LoggingService loggingService) {
        this.loggingService = loggingService;
    }

    public Optional<Quote> getQuote(String stockSymbol, String userId, int transactionNum) {
        String quoteServerUrl = "http://quoteserve.seng.uvic.ca:4452";
        Map<String, String> params = ImmutableMap.of("stockSymbol", stockSymbol, "userId", userId);
        RestTemplate restTemplate = new RestTemplate();

        String response = restTemplate.getForObject(quoteServerUrl, String.class, params);
        if (response == null) {
            return Optional.empty();
        }
        String[] responseList = response.split(",");
        // Valid response list should be a length of 5.
        if (responseList.length < 5) {
            return Optional.empty();
        }

        int price = extractPriceFromResponseList(responseList);
        Long quoteServerTime = extractQuoteServerTimeFromResponseList(responseList);
        String cryptoKey = responseList[4];

        Quote quote = Quote.builder()
                .stockSymbol(stockSymbol)
                .userId(userId)
                .price(price)
                .timestamp(LocalDateTime.now())
                .cryptoKey(cryptoKey)
                .build();

        loggingService.logQuoteServer(
                QuoteServerLog.builder()
                        .price(quote.getPrice())
                        .username(userId)
                        .quoteServerTime(quoteServerTime)
                        .timestamp(System.currentTimeMillis())
                        .transactionNum(transactionNum)
                        .stockSymbol(stockSymbol)
                        .cryptokey(quote.getCryptoKey())
                        .build());
        return Optional.of(quote);
    }

    private Integer extractPriceFromResponseList(String[] responseList) {
        return (int) (Double.parseDouble(responseList[0]) * 100);
    }

    private Long extractQuoteServerTimeFromResponseList(String[] responseList) {
        String responseTimestamp = responseList[3];
        return Long.parseLong(responseTimestamp);
    }
}