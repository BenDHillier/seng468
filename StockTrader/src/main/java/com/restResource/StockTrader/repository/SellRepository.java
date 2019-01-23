package com.restResource.StockTrader.repository;

import com.restResource.StockTrader.entity.PendingSell;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface SellRepository extends CrudRepository<PendingSell, Integer> {
    @Query(value =
            "WITH users_sells AS (" +
            "SELECT * FROM pending_sell WHERE user_id = ?1) " +
            "SELECT * FROM users_sells " +
            "WHERE timestamp = (SELECT  MAX(timestamp) FROM users_sells)",
            nativeQuery = true)
    Optional<PendingSell> findMostRecentForUserId(String userId);
}
