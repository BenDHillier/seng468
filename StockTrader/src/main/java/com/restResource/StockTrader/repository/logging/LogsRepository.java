package com.restResource.StockTrader.repository.logging;

import com.restResource.StockTrader.entity.logging.CentralLog;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.xml.bind.annotation.XmlElement;

@Repository
public interface LogsRepository extends CrudRepository<CentralLog,Integer> {
    @Query(value =
            "SELECT * FROM logs WHERE username = ?1",
            nativeQuery = true)
    Iterable<CentralLog> findAllLogsForUser(String username);

    @Query(value =
            "SELECT * FROM logs",
            nativeQuery = true)
    Iterable<CentralLog> findAllLogs();
}
