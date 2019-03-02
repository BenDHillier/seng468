package com.restResource.StockTrader.entity;

import com.restResource.StockTrader.entity.converter.LocalDateTimeToEpochConverter;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;


/**
 * Class for a SellRepository table entry.
 */
@Builder(toBuilder = true)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PendingSell {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    Integer id;

    @Min(value = 0)
    Integer stockCount;
    Integer stockPrice;
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
