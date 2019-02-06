package com.restResource.StockTrader.entity.logging;
import com.restResource.StockTrader.entity.CommandType;
import lombok.*;
import javax.persistence.*;
import javax.xml.bind.annotation.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "systemEvent")
public class UserCommandLog {
    @XmlTransient
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    Integer transactionNum;
    @XmlElement
    Long timestamp;
    @XmlElement
    String server;
    @XmlElement
    @Enumerated(EnumType.STRING)
    CommandType command;
    @XmlElement
    String username;
    @XmlElement
    String stockSymbol;
    @XmlElement
    String filename;
    @XmlElement
    Integer funds;
}
