package com.restResource.StockTrader.entity.logging;

import com.restResource.StockTrader.entity.CommandType;
import lombok.*;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

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
    //@XmlTransient
//    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Id
    Integer transactionNum;
    Long timestamp;
    String username;
}

//@Entity
//@Data
//@Getter
//@NoArgsConstructor
//@XmlAccessorType(XmlAccessType.FIELD)
//public class EventLog extends Log{
//
//    //All have timestamp, server, transactionNum, username,
//
//    private String command;
//    @Builder(toBuilder = true)
//    public EventLog(String command, Long timestamp, String logtype, String server, Integer transactionNum) {
//        super(transactionNum,timestamp,logtype);
//        this.command = command;
//    }
//}
