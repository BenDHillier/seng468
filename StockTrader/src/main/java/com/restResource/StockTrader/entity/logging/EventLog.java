package com.restResource.StockTrader.entity.logging;

import com.restResource.StockTrader.entity.CommandType;
import lombok.*;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

/* TODO: This class isn't used at the moment, but it could become useful if we end up optimizing xml
* */

@Builder(toBuilder = true)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class EventLog {
    String action;
    String cryptokey;
    @Enumerated(EnumType.STRING)
    CommandType command;
    String debugMessage;
    String errorMessage;
    String filename;
    Integer funds;
    @XmlTransient
    String logtype;
    Integer price;
    Long quoteServerTime;
    String server;
    String stockSymbol;;
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Id
    Integer transactionNum;
    Long timestamp;
    String username;
}
