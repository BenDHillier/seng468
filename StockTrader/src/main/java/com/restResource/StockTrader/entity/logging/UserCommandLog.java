package com.restResource.StockTrader.entity.logging;
import com.restResource.StockTrader.entity.CommandType;
import lombok.*;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;


@Entity
@Data
@Getter
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class UserCommandLog {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    Integer transactionNum;
    Long timestamp;
    String server;
    @Enumerated(EnumType.STRING)
    CommandType command;
    String username;
    String stockSymbol;
    String filename;
    Integer funds;


}
