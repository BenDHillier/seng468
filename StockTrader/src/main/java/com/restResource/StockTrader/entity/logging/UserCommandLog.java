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
    @XmlElement
    @Builder.Default
    Integer transactionNum = -1;
    @XmlElement
    @Builder.Default
    Long timestamp = System.currentTimeMillis();
    @XmlElement
    @Builder.Default
    String server = "DEFAULT_SERVER_VAL";
    @XmlElement
    @Builder.Default
    @Enumerated(EnumType.STRING)
    CommandType command = CommandType.NONE;
    @XmlElement
    String username; //can be null
    @XmlElement
    String stockSymbol; //can be null
    @XmlElement
    String filename; //can be null
    @XmlElement
    Integer funds; //can be null
}
