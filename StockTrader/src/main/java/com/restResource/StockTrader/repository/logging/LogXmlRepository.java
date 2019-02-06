package com.restResource.StockTrader.repository.logging;

import com.restResource.StockTrader.entity.logging.LogXml;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogXmlRepository extends CrudRepository<LogXml,Integer> {
    @Query(value =
            "SELECT xml_log_entry FROM event_log WHERE user_id = ?1",
            nativeQuery = true)
    Iterable<LogXml> findAllLogsForUser(String userId);

    @Query(value =
            "SELECT xml_log_entry FROM event_log",
            nativeQuery = true)
    Iterable<LogXml> findAllLogs();
}
