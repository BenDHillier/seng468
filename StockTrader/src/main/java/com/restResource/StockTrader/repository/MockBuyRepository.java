package com.restResource.StockTrader.repository;

import com.restResource.StockTrader.entity.BuyEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MockBuyRepository implements BuyRepository {
    private Map<Integer, BuyEntity> map;

    public MockBuyRepository() {
        map = new HashMap<>();
    }

    @Override
    public BuyEntity save(BuyEntity buyEntity) {
        map.put(buyEntity.getId(), buyEntity);
        return buyEntity;
    }

    @Override
    public Optional<BuyEntity> findById(Integer integer) {
        return Optional.ofNullable(map.get(integer));
    }
}
