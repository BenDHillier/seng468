package com.restResource.StockTrader.repository;

import com.restResource.StockTrader.entity.BuyTrigger;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface BuyTriggerRepository extends CrudRepository<BuyTrigger, String> {
    @Modifying
    @Transactional
    @Query(value =
            "INSERT INTO buy_trigger (user_id, stock_symbol, stock_amount) VALUES (?1, ?2, ?3) " +
                    "ON CONFLICT (user_id, stock_symbol) DO UPDATE " +
                    "SET stock_amount = ?3 ",
            nativeQuery = true)
    void setBuyTriggerAmount(String userId, String stockSymbol, Integer amount);


    @Modifying
    @Transactional
    @Query(value =
        "INSERT INTO buy_trigger (user_id, stock_symbol, stock_cost) VALUES (?1, ?2, ?3) " +
                "ON CONFLICT (user_id, stock_symbol) DO UPDATE " +
                "SET stock_cost = ?3 ",
            nativeQuery = true)
    void setBuyTriggerCost(String userId, String stockSymbol, Integer cost);

    @Query(value=
            "Select * from buy_trigger where user_id = ?1 and stock_symbol=?2",
            nativeQuery = true)
    Optional<BuyTrigger> findByUserIdAndStockSymbol(String userId, String stockSymbol);
}
