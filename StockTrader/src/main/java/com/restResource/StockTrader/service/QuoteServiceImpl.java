package com.restResource.StockTrader.service;

import java.io.*;
import java.net.*;
import com.google.common.collect.ImmutableMap;
import java.time.LocalDateTime;
import com.restResource.StockTrader.entity.Quote;
import com.restResource.StockTrader.entity.converter.LocalDateTimeToEpochConverter;
import com.restResource.StockTrader.entity.logging.QuoteServerLog;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;

@Service
@Profile("prod")
public class QuoteServiceImpl implements QuoteService {

    private LoggingService loggingService;
    private static String quoteServerHost = "quoteserve.seng.uvic.ca";
    private static int quoteServerPort = 4452;


    public QuoteServiceImpl(LoggingService loggingService) {
        this.loggingService = loggingService;
    }

    public Optional<Quote> getQuote(String stockSymbol, String userId, int transactionNum) {
	String response;
	try {
		Socket socket = new Socket(quoteServerHost, quoteServerPort);
		PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        	BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out.println(stockSymbol+","+userId);
		response = in.readLine();
		out.close();
        	in.close();
        	socket.close();
	} catch (IOException e) {
		System.out.println(e.getMessage());
		return Optional.empty();
	}
	System.out.println("response: "+response);

        if (response == null || response.equals("")) {
            return Optional.empty();
        }
        String[] responseList = response.split(",");
        // Valid response list should be a length of 5.
        if (responseList.length < 5) {
            return Optional.empty();
        }

        int price = extractPriceFromResponseList(responseList);
        LocalDateTime quoteServerTime = extractQuoteServerTimeFromResponseList(responseList);
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
                        .timestamp(quote.getTimestamp())
                        .transactionNum(transactionNum)
                        .stockSymbol(stockSymbol)
                        .cryptokey(quote.getCryptoKey())
                        .build());
        return Optional.of(quote);
    }

    private Integer extractPriceFromResponseList(String[] responseList) {
        return (int) (Double.parseDouble(responseList[0]) * 100);
    }

    private LocalDateTime extractQuoteServerTimeFromResponseList(String[] responseList) {
        String responseTimestamp = responseList[3];
        LocalDateTimeToEpochConverter converter = new LocalDateTimeToEpochConverter();
        return converter.convertToEntityAttribute(Long.parseLong(responseTimestamp));
    }
}
