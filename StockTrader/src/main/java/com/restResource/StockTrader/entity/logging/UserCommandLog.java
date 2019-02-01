package com.restResource.StockTrader.entity.logging;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Builder(toBuilder = true)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCommandLog {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    Integer id;
    LocalDateTime timestamp;
    String server;
    String command;
    String username;
    String stockSymbol;
    String filename;
    Integer funds;
}
