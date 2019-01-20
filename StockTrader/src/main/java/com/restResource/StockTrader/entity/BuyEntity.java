package com.restResource.StockTrader.entity;

import lombok.Builder;
import lombok.Value;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

/**
 * Class for a BuyRepository table entry.
 */
@Entity
@Value
@Builder(toBuilder = true)
public class BuyEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer id;

    private String userId;

    private LocalDateTime timestamp;

    private String stockSymbol;

    @Builder.Default
    private Boolean buyCompleted = false;

}
