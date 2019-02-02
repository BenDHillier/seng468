package com.restResource.StockTrader.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Min;
import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * Class for a AccountRepository table entry.
 */
@Builder(toBuilder = true)
@Entity
@IdClass(CompositeKey.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BuyTrigger {
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

