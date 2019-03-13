package com.restResource.StockTrader.service;

import java.io.*;
import java.net.*;

import com.github.jedis.lock.JedisLock;
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
import redis.clients.jedis.Jedis;

import java.time.ZoneId;
import java.time.temporal.TemporalUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Profile("prod")
public class QuoteServiceImpl implements QuoteService {

    private LoggingService loggingService;
    private Jedis jedis;

    private static Cache<String,Quote> quoteCache = CacheBuilder.newBuilder()
            .expireAfterWrite(50, TimeUnit.SECONDS)
            .maximumSize(1000)
            .initialCapacity(1000)
            .build();

    private static final Queue<Socket> quoteConnections = new LinkedList<>();
    private static final int CONNECTION_COUNT = 5;

    public QuoteServiceImpl(LoggingService loggingService) {
        this.loggingService = loggingService;
        createConnections();
        this.jedis = jedis;
    }

    private void createConnections() {
        for (int i = 0; i < CONNECTION_COUNT; ++i) {
            Socket s = createConnection();
            if (s != null) {
                quoteConnections.add(s);
            }
        }
    }

    private Socket createConnection() {
        String quoteServerHost = "quoteserve.seng.uvic.ca";
        int quoteServerPort = 4452;
        try {
            return new Socket(quoteServerHost, quoteServerPort);
        } catch(Exception e) {
            return null;
        }
    }

    private Socket acquireConnection() {
        Socket s;
        synchronized (quoteConnections) {
            if (quoteConnections.size() > 0) {
                s = quoteConnections.poll();
            } else {
                s = createConnection();
            }
        }
        return s;
    }

    private void returnConnection(Socket s) {
        synchronized (quoteConnections) {
            quoteConnections.add(s);
        }
    }

    private Quote getQuoteFromServer(String stockSymbol, String userId, int transactionNum) throws Exception {
        //create and aquire a lock for the stock symbol
        String lockkey = stockSymbol+"_lock";
        //lock will time out after 10 seconds and expire after 50
        JedisLock lock = new JedisLock(jedis, lockkey, 10000, 50000);
        lock.acquire();
        try {
            //check redis
            String response = jedis.get(stockSymbol);
            boolean isNew = false;

            //if redis doesnt have the response, grab it from the quote server
            if (response == null) {
                isNew = true;
                Socket socket = acquireConnection();
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out.println(stockSymbol + "," + userId);
                response = in.readLine();
                out.close();
                in.close();
                returnConnection(socket);
                if (response == null || response.equals("")) {
                    throw new IllegalStateException("response not valid");
                }
                //assign the stock symbol the unparsed response
                jedis.set(stockSymbol, response);
                //give it a lifespan of 50 seconds
                jedis.expire(stockSymbol, 50);
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
            if (isNew) {
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
            }
            return quote;
        } finally {
            lock.release();
        }
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
