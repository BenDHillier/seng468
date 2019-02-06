package com.restResource.StockTrader.bean;

import com.restResource.StockTrader.entity.logging.EventLog;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name="log")
@XmlAccessorType(XmlAccessType.FIELD)
public class EventLogs {


    @Getter
    @Setter
    @XmlElement(name="userCommand")
    private List<EventLog> userCommandList = new ArrayList<>();

    @Getter
    @Setter
    @XmlElement(name="quoteServer")
    private List<EventLog> quoteServerLogList = new ArrayList<>();

    @Getter
    @Setter
    @XmlElement(name="accountTransaction")
    private List<EventLog> accountTransactionLogList = new ArrayList<>();

    @Getter
    @Setter
    @XmlElement(name="systemEvent")
    private List<EventLog> systemEventLogList = new ArrayList<>();

    @Getter
    @Setter
    @XmlElement(name="errorEvent")
    private List<EventLog> errorEventLogList = new ArrayList<>();

    @Getter
    @Setter
    @XmlElement(name="debugEvent")
    private List<EventLog> debugEventLogList = new ArrayList<>();

    @Getter
    @Setter
    private List<EventLog> everyLogList = new ArrayList<>();
}