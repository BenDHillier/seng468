package com.restResource.StockTrader.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@Table(name = "account_transaction_log")
public class AccountTransaction {
    @Id
    Integer id;
    String action;
    Integer funds;
    Long timestamp;
}
