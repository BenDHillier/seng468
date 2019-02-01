package com.restResource.StockTrader.repository.logging;

import com.restResource.StockTrader.entity.logging.UserCommandLog;
import org.springframework.data.repository.CrudRepository;

public interface UserCommandLogRepository extends CrudRepository<UserCommandLog, Integer> {
}
