package com.restResource.StockTrader.entity.logging;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.restResource.StockTrader.entity.CommandType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.xml.bind.annotation.*;

@Builder(toBuilder = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "errorEvent")
public class ErrorEventLog {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    Integer transactionNum;
    @XmlElement
    @Builder.Default
    Long timestamp = System.currentTimeMillis();
    @XmlElement
    @Builder.Default
    String server = "";
    @XmlElement
    @Builder.Default
    CommandType command = CommandType.NONE;
    @XmlElement
    @Builder.Default
    String userName = "";
    @XmlElement
    @Builder.Default
    String stockSymbol = "";
    @XmlElement
    @Builder.Default
    Integer funds = 0;
    @XmlElement
    @Builder.Default
    String errorMessage = "";
    @XmlElement
    @Builder.Default
    String fileName = "";
}
