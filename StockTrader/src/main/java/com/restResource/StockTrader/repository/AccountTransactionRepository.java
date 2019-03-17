package com.restResource.StockTrader.repository;

import com.restResource.StockTrader.entity.AccountTransaction;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.List;

public interface AccountTransactionRepository extends Repository<AccountTransaction, Integer> {

    @Query(value = "SELECT id,action,funds,timestamp " +
                   "FROM account_transaction_log WHERE username = ?1",
           nativeQuery = true)
    List<AccountTransaction> findByUsername(String username);
}
