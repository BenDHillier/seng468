package com.restResource.StockTrader.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;


/**
 * Class for a SellTrigger table entry.
 */
@Builder(toBuilder = true)
@Entity
@IdClass(TriggerKey.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SellTrigger {
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

