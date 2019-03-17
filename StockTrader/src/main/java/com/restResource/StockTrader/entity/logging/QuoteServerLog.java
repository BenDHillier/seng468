package com.restResource.StockTrader.entity.logging;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

import com.restResource.StockTrader.entity.converter.LocalDateTimeToEpochConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.time.LocalDateTime;

@Builder(toBuilder = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "quoteServer")
public class QuoteServerLog {
    @XmlElement
    @Builder.Default
    Integer transactionNum = -1;
    @XmlElement
    //@Builder.Default
    //@Convert(converter = LocalDateTimeToEpochConverter.class)
    Long timestamp;
    @XmlElement
    //@Builder.Default
    //@Convert(converter = LocalDateTimeToEpochConverter.class)
    Long quoteServerTime;
    @XmlElement
    @Builder.Default
    String username = "DEFAULT_USERNAME";
    @XmlElement
    @Builder.Default
    String stockSymbol = "DEFAULT_STOCK_SYMBOL";
    @XmlElement
    @Builder.Default
    String price = "0.00";
    @XmlElement
    @Builder.Default
    String cryptokey = "DEFAULT_CRYPTOKEY";
    @XmlElement
    @Builder.Default
    String server = "DEFAULT_SERVER";
}
