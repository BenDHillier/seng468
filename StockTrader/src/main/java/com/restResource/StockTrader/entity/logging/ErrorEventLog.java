package com.restResource.StockTrader.entity.logging;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
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
public class ErrorEventLog {

    @JacksonXmlProperty
    Long timestamp;
    @JacksonXmlProperty
    String server;
    @JacksonXmlProperty
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    Integer transactionNum;
    @JacksonXmlProperty
    String command;
    @JacksonXmlProperty
    String userName;
    @JacksonXmlProperty
    String stockSymbol;
    @JacksonXmlProperty
    Integer funds;
    @JacksonXmlProperty
    String errorMessage;
}
