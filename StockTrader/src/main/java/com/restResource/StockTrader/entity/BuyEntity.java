package com.restResource.StockTrader.entity;

import lombok.Builder;
import lombok.Value;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@Value
@Builder
public class BuyEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer id;

    private String userId;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    private String stockSymbol;

    @Builder.Default
    private Boolean buyCompleted = false;

}
