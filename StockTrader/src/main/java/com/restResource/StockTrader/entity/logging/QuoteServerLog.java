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
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Builder(toBuilder = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "quoteServer")
public class QuoteServerLog {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    Integer transactionNum;
    @XmlElement
    @Builder.Default
    Long timestamp = System.currentTimeMillis();
    @XmlElement
    @Builder.Default
    Long quoteServerTime = System.currentTimeMillis();
    @XmlElement
    @Builder.Default
    String userName = "";
    @XmlElement
    @Builder.Default
    String stockSymbol = "";
    @XmlElement
    @Builder.Default
    Integer price = 0;
    @XmlElement
    @Builder.Default
    String cryptokey = "";
    @XmlElement
    @Builder.Default
    String server = "";
}
