package com.restResource.StockTrader.entity;

import com.restResource.StockTrader.entity.converter.LocalDateTimeToEpochConverter;
import lombok.*;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;


/**
 * Class for a AccountRepository table entry.
 */
@Builder(toBuilder = true)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    @Min(value = 0)
    Integer amount;
    @Id //sets the pkey to userId
    String userId;
    Integer lastTransactionNumber; //the last transaction to touch the account
    String lastServer; //the last server to touch the account
    @Convert(converter = LocalDateTimeToEpochConverter.class)
    @NonNull
    LocalDateTime lastTransactionTime;
}
