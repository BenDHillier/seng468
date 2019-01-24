package com.restResource.StockTrader.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
    LocalDateTime timestamp;

    public boolean isExpired() {
        return timestamp.isBefore(LocalDateTime.now().minusMinutes(1));
    }
}
