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
@XmlRootElement(name = "debugEvent")
public class DebugEventLog {
    @XmlElement
    @Builder.Default
    Integer transactionNum = 0;
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
    String debugMessage = "";
    @XmlElement
    @Builder.Default
    String fileName = "";
}
