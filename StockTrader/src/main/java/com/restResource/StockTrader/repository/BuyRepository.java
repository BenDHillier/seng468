package com.restResource.StockTrader.repository;

import com.restResource.StockTrader.entity.PendingBuy;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BuyRepository extends CrudRepository<PendingBuy, Integer> {
    @Query(value = "SELECT * FROM pending_buy WHERE user_id = ?1 AND timestamp = (SELECT  MAX(timestamp) FROM pending_buy)",
            nativeQuery = true)
    Optional<PendingBuy> findMostRecentForUserId(String userId);
}
