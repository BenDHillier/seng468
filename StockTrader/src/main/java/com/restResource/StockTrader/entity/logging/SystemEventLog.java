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

@Builder(toBuilder = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "systemEvent")
public class SystemEventLog {
    @XmlElement
    @Builder.Default
    Integer transactionNum = -1;
    @XmlElement(required = true)
    @Builder.Default
    CommandType command = CommandType.NONE;
    @XmlElement
    String username;//can be null
    @XmlElement
    String stockSymbol;//can be null
    @XmlElement
    String filename;//can be null
    @XmlElement
    Integer funds; //can be null
    @XmlElement
    @Builder.Default
    Long timestamp = System.currentTimeMillis();
    @XmlElement
    @Builder.Default
    String server = "DEFAULT_SERVER";
}
