package com.restResource.StockTrader.entity.logging;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.restResource.StockTrader.entity.CommandType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder(toBuilder = true)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCommandLog {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    Integer transactionNum;
    @JacksonXmlProperty
    LocalDateTime timestamp;
    @JacksonXmlProperty
    String server;
    @JacksonXmlProperty
    @Enumerated(EnumType.STRING)
    CommandType command;
    @JacksonXmlProperty
    String username;
    @JacksonXmlProperty
    String stockSymbol;
    @JacksonXmlProperty
    String filename;
    @JacksonXmlProperty
    Integer funds;
}
