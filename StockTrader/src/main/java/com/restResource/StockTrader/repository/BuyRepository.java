package com.restResource.StockTrader.repository;

import com.restResource.StockTrader.entity.PendingBuy;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface BuyRepository extends CrudRepository<PendingBuy, Integer> {
    @Query(value =
            "WITH users_buys AS (" +
            "SELECT * FROM pending_buy WHERE user_id = ?1) " +
            "SELECT * FROM users_buys " +
            "WHERE time_created = (SELECT  MAX(time_created) FROM users_buys)",
            nativeQuery = true)
    Optional<PendingBuy> findMostRecentForUserId(String userId);
}
