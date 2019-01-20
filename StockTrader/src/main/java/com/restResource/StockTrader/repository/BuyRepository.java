package com.restResource.StockTrader.repository;

import com.restResource.StockTrader.entity.BuyEntity;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface BuyRepository extends Repository<BuyEntity, Integer> {
    BuyEntity save(BuyEntity buyEntity);

    Optional<BuyEntity> findById(Integer integer);
}
