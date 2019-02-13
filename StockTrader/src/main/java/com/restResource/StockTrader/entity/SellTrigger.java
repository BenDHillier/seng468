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
@IdClass(CompositeKey.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SellTrigger {
    @Min(value = 0)
    Integer stock_amount;
    @Min(value = 0)
    Integer stock_cost;

    @Id
    String stock_symbol;
    @Id
    String user_id;
    LocalDateTime timestamp;

    public boolean isExpired() {
        return timestamp.isBefore(LocalDateTime.now().minusMinutes(1));
    }
}

