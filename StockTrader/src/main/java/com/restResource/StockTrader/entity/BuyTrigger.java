package com.restResource.StockTrader.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;


/**
 * Class for a BuyTrigger table entry.
 */
@Builder(toBuilder = true)
@Entity
@IdClass(TriggerKey.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BuyTrigger {
    @Min(value = 0)
    Integer stockAmount;
    @Min(value = 0)
    Integer stockCost;

    @Id
    String stockSymbol;
    @Id
    String userId;
    LocalDateTime timestamp;

    public boolean isExpired() {
        return timestamp.isBefore(LocalDateTime.now().minusMinutes(1));
    }
}

