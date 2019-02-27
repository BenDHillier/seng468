package com.restResource.StockTrader.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.validation.constraints.Min;

@Builder(toBuilder = true)
@Entity
@Data
@IdClass(InvestmentId.class)
@NoArgsConstructor
@AllArgsConstructor
public class Investment {
    @Id
    String owner;
    @Id String stockSymbol;
    @Min(value = 0)
    Integer stockCount;
}
