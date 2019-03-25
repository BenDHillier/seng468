package com.restResource.StockTrader.entity.logging;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

//            stmt.setString(1, command);
//                    stmt.setString(2, timestamp);
//                    stmt.setString(3, quoteServerTime);
//                    stmt.setString(4, server);
//                    stmt.setString(5, transactionNum);
//                    stmt.setString(6, username);
//                    stmt.setString(7, stockSymbol);
//                    stmt.setString(8, filename);
//                    stmt.setString(9, funds);
//                    stmt.setString(10, price);
//                    stmt.setString(11, cryptokey);
//                    stmt.setString(12, errorMessage);
//                    stmt.setString(13, debugMessage);
//                    stmt.setString(14, logtype);

@Builder(toBuilder = true)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CentralLog {
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Id
    Integer event_id;
    String logtype;
    String command;
    String timestamp;
    String quote_server_time;
    String server;
    String transaction_num;
    String username;
    String stock_symbol;
    String filename;
    String funds;
    String price;
    String cryptokey;
    String error_message;
    String debug_message;
    String action;
}
