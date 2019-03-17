package com.restResource.StockTrader.entity;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Builder(toBuilder = true)
@Data
public class DisplaySummary implements Serializable {
    String userId;
    Integer amount;
    List<Investment> investments;
    List<BuyTrigger> buyTriggers;
    List<SellTrigger> sellTriggers;
    List<AccountTransaction> accountTransactions;
}
