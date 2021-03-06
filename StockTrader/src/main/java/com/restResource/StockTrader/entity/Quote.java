package com.restResource.StockTrader.entity;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class Quote {
    Integer price;
    String stockSymbol;
    String userId;
    LocalDateTime timestamp;
    String cryptoKey;
}
