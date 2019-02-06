package com.restResource.StockTrader.entity.logging;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Builder(toBuilder = true)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogXml {
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Id
    Integer id;
    String xmlLogEntry;
    String userId;
}
