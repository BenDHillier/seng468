package com.restResource.StockTrader.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.validation.constraints.Min;

@Builder(toBuilder = true)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Investment {
    @EmbeddedId
    InvestmentId investmentId;
    @Min(value = 0)
    Integer stockCount;
}
