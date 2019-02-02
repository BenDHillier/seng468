package com.restResource.StockTrader.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.restResource.StockTrader.entity.logging.AccountTransactionLog;
import com.restResource.StockTrader.entity.logging.ErrorEventLog;
import com.restResource.StockTrader.entity.logging.QuoteServerLog;
import lombok.*;

import com.restResource.StockTrader.entity.logging.UserCommandLog;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.ArrayList;

/* A bean to help with making the XML output look nice
* */
@JacksonXmlRootElement(localName = "log")
@NoArgsConstructor
@AllArgsConstructor
public class UserCommandLogs {

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "userCommand")
    @Getter
    @Setter
    private Iterable<UserCommandLog> userCommandLogList = new ArrayList<>();

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "accountTransaction")
    @Getter
    @Setter
    private Iterable<AccountTransactionLog> accountTransactionLogList = new ArrayList<>();

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "quoteServer")
    @Getter
    @Setter
    private Iterable<QuoteServerLog> quoteServerTransactionLogList = new ArrayList<>();

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "errorEvent")
    @Getter
    @Setter
    private Iterable<ErrorEventLog> errorEventLogList = new ArrayList<>();
}