package com.restResource.StockTrader.repository.logging;

import com.restResource.StockTrader.entity.logging.QuoteServerLog;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuoteServerLogRepository extends CrudRepository<QuoteServerLog, Integer> {
}
