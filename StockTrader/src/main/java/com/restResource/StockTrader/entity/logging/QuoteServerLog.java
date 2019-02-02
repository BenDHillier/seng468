package com.restResource.StockTrader.entity.logging;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
//import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder(toBuilder = true)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuoteServerLog {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    Integer transactionNum;
    @JacksonXmlProperty
    Long timestamp;
    @JacksonXmlProperty
    Long quoteServerTime;
    @JacksonXmlProperty
    String userName;
    @JacksonXmlProperty
    String stockSymbol;
    @JacksonXmlProperty
    Integer price;
    @JacksonXmlProperty
    String cryptokey;
    @JacksonXmlProperty
    String server;
}
