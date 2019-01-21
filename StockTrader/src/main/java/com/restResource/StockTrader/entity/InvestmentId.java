package com.restResource.StockTrader.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Builder(toBuilder = true)
@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class InvestmentId implements Serializable {
    String owner;
    String stockSymbol;
}
