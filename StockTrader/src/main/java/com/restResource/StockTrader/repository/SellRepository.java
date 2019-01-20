package com.restResource.StockTrader.repository;

import com.restResource.StockTrader.entity.PendingSell;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface SellRepository extends Repository<PendingSell, Integer> {
    PendingSell save(PendingSell pendingSell);

    Optional<PendingSell> findById(Integer integer);
}
