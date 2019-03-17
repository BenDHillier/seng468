package com.restResource.StockTrader.entity;

import com.restResource.StockTrader.entity.converter.LocalDateTimeToEpochConverter;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Min;
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
    @Min(value = 0)
    Integer amount;
    String stockSymbol;
    String userId;
    @Convert(converter = LocalDateTimeToEpochConverter.class)
    LocalDateTime timestamp;
    @Convert(converter = LocalDateTimeToEpochConverter.class)
    LocalDateTime timeCreated;


    public boolean isExpired() {
        return timestamp.isBefore(LocalDateTime.now().minusMinutes(1));
    }
}
