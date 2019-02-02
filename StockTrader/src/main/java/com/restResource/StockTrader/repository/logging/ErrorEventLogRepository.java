package com.restResource.StockTrader.repository.logging;

import com.restResource.StockTrader.entity.logging.ErrorEventLog;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ErrorEventLogRepository extends CrudRepository<ErrorEventLog, Integer> {
}
