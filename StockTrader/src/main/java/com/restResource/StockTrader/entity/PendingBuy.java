package com.restResource.StockTrader.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;


/**
 * Class for a BuyRepository table entry.
 */
@Builder(toBuilder = true)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PendingBuy {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    Integer id;

    Integer price;
    String stockSymbol;
    String userId;
    LocalDateTime timestamp;
}
