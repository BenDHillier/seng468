package com.restResource.StockTrader.repository.logging;

import com.restResource.StockTrader.entity.logging.UserCommandLog;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
//import org.springframework.stereotype.Repository;

//TODO Create ERROR logging just like UserCommandLogRepo etc...
//TODO Make Database to XML dump working to serve dumplog files to client

public interface UserCommandLogRepository extends CrudRepository<UserCommandLog, Integer> {
    @Query(value =
            "SELECT * FROM user_command_log WHERE logtype = ?1",
            nativeQuery = true)
    UserCommandLog findAllWithLogtype(String logtype);
}
