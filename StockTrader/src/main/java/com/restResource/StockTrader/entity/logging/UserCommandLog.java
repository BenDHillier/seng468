package com.restResource.StockTrader.entity.logging;

import com.restResource.StockTrader.entity.CommandType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder(toBuilder = true)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCommandLog {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    Integer transactionNum;
    LocalDateTime timestamp;
    String server;
    @Enumerated(EnumType.STRING)
    CommandType command;
    String username;
    String stockSymbol;
    String filename;
    Integer funds;
}
