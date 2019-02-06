package com.restResource.StockTrader.entity.logging;

import com.restResource.StockTrader.entity.CommandType;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
@XmlRootElement(name = "systemEvent")
public class SystemEventLog {
    @XmlTransient
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    Integer transactionNum;
    @XmlElement
    CommandType command;
    @XmlElement
    String username;
    @XmlElement
    String stockStymbol;
    @XmlElement
    String filename;
    @XmlElement
    Integer funds;
    @XmlElement
    Long timestamp;
    @XmlElement
    String server;
}
