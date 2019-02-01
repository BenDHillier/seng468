package com.restResource.StockTrader.bean;

import lombok.*;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.restResource.StockTrader.entity.logging.UserCommandLog;

import java.util.ArrayList;
//import java.util.List;

/* A bean to help with making the XML output look nice
* */
@JacksonXmlRootElement(localName = "log")
@NoArgsConstructor
@AllArgsConstructor
public class UserCommandLogs {

    @JacksonXmlProperty(localName = "userCommand")
    @JacksonXmlElementWrapper(useWrapping = false)
    @Getter
    @Setter
    private Iterable<UserCommandLog> userCommandLogList = new ArrayList<>();
}