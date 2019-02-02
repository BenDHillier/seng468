package com.restResource.StockTrader.repository.logging;

import com.restResource.StockTrader.entity.logging.AccountTransactionLog;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountTransactionLogRepository  extends CrudRepository<AccountTransactionLog, Integer> {
}
