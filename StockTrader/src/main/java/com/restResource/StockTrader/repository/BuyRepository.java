package com.restResource.StockTrader.repository;

import com.restResource.StockTrader.entity.PendingBuy;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import java.util.Optional;

public interface BuyRepository extends Repository<PendingBuy, Integer> {
    PendingBuy save(PendingBuy pendingBuy);

    Optional<PendingBuy> findById(Integer integer);

    @Query("FROM PendingBuy WHERE user_id = userId AND timestamp = (SELECT  MAX(timestamp) FROM PendingBuy)")
    Optional<PendingBuy> findBuyToCommitForUserId(@Param("userId") String userId);
}
