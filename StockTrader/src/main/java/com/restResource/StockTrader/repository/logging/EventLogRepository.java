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
    EventLog findAllWithLogtype(String logtype);
}
