package com.restResource.StockTrader.repository;

import com.restResource.StockTrader.entity.Account;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

public interface AccountRepository extends CrudRepository<Account, String> {
    @Modifying
    @Transactional
    @Query(value =
            "INSERT INTO account VALUES (?1, ?2) " +
                    "ON CONFLICT (user_id) DO UPDATE " +
                    "SET amount = account.amount + ?2 ",
        nativeQuery = true)
    void updateAccountBalance(String userId, Integer amount);
}