package com.restResource.StockTrader.repository.logging;

import com.restResource.StockTrader.entity.logging.EventLog;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventLogRepository extends CrudRepository<EventLog, Integer> {
    @Query(value =
            "SELECT * FROM event_log WHERE logtype = ?1",
            nativeQuery = true)
    Iterable<EventLog> findAllWithLogtype(String logtype);

    @Query(value =
            "SELECT xmlelement(name \"userCommand\", " +
            "(select xmlforest(transaction_num,timestamp,server, " +
            "command,username,stock_symbol,filename,funds,logtype) " +
            " from event_log))",
            nativeQuery = true)
    Iterable<String> getAllUserCommands();



    //select xmlelement(name "userCommand",
    //  (select xmlforest(action,cryptokey,command,debug_message,
    //                    error_message,filename,funds,logtype,
    //                    price,quote_server_time,server,stock_symbol,
    //                    transaction_num,timestamp,username) from event_log));
}
