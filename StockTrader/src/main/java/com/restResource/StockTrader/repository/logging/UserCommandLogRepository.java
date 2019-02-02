package com.restResource.StockTrader.repository.logging;

import com.restResource.StockTrader.entity.logging.UserCommandLog;
import org.springframework.data.repository.CrudRepository;
//import org.springframework.stereotype.Repository;

//TODO Create ERROR logging just like UserCommandLogRepo etc...
//TODO Make Database to XML dump working to serve dumplog files to client

public interface UserCommandLogRepository extends CrudRepository<UserCommandLog, Integer> {
}
