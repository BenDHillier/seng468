package com.restResource.StockTrader.entity.logging;

import com.restResource.StockTrader.entity.CommandType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.xml.bind.annotation.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "debugEvent")
public class DebugEventLog {
    @XmlTransient
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    Integer transactionNum;
    @XmlElement
    Long timestamp;
    @XmlElement
    String server;
    @XmlElement
    CommandType command;
    @XmlElement
    String userName;
    @XmlElement
    String stockSymbol;
    @XmlElement
    Integer funds;
    @XmlElement
    String debugMessage;
    @XmlElement
    String fileName;
}
