package com.restResource.StockTrader.entity.logging;
import com.restResource.StockTrader.entity.CommandType;
import lombok.*;
import javax.persistence.*;
import javax.xml.bind.annotation.*;

@Builder(toBuilder = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "userCommand")
public class UserCommandLog {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    Integer transactionNum;
    @XmlElement
    @Builder.Default
    Long timestamp = System.currentTimeMillis();
    @XmlElement
    @Builder.Default
    String server = "";
    @XmlElement
    @Builder.Default
    @Enumerated(EnumType.STRING)
    CommandType command = CommandType.NONE;
    @XmlElement
    @Builder.Default
    String username = "";
    @XmlElement
    @Builder.Default
    String stockSymbol = "";
    @XmlElement
    @Builder.Default
    String filename = "";
    @XmlElement
    @Builder.Default
    Integer funds = 0;
}
