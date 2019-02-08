package com.restResource.StockTrader.repository.logging;

import com.restResource.StockTrader.entity.logging.LogXml;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.xml.bind.annotation.XmlElement;

@Repository
public interface LogXmlRepository extends CrudRepository<LogXml,Integer> {
    @Query(value =
            "SELECT xml_log_entry FROM log_xml WHERE user_id = ?1",
            nativeQuery = true)
    Iterable<String> findAllLogsForUser(String userId);

    @Query(value =
            "SELECT xml_log_entry FROM log_xml",
            nativeQuery = true)
    Iterable<String> findAllLogs();
}
