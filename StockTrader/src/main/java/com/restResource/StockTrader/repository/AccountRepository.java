package com.restResource.StockTrader.repository;

import com.restResource.StockTrader.entity.Account;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface AccountRepository extends CrudRepository<Account, Integer> {
    @Query(value =
        "UPDATE account SET amount  = amount + (?2)" +
            "WHERE user_id = ?1",
        nativeQuery = true)
    Optional<Account> updateAccountBalance(String userId, int amount);
}
