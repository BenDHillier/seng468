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
public class TriggerKey implements Serializable {
    private String userId;
    private String stockSymbol;
}
