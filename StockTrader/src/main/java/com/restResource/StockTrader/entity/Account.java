package com.restResource.StockTrader.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;


/**
 * Class for a AccountRepository table entry.
 */
@Builder(toBuilder = true)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    //@GeneratedValue(strategy= GenerationType.AUTO)
    Integer amount;
    @Id
    String userId;
}
