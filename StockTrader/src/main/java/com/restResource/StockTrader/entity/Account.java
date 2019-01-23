package com.restResource.StockTrader.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.Min;


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
}
