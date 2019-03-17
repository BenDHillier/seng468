package com.restResource.StockTrader.repository;

import com.restResource.StockTrader.entity.SellTrigger;
import com.restResource.StockTrader.entity.TriggerKey;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface SellTriggerRepository extends CrudRepository<SellTrigger, TriggerKey> {
    @Query(value=
            "Select * from sell_trigger where user_id = ?1 and stock_symbol=?2 ",
            nativeQuery = true)
    Optional<SellTrigger> findByUserIdAndStockSymbol(String userId, String stockSymbol);

    @Query(value= "SELECT * FROM sell_trigger WHERE user_id = ?1", nativeQuery = true)
    List<SellTrigger> findByUserId(String userId);

    @Transactional
    @Modifying
    @Query(value =
            "UPDATE sell_trigger SET stock_amount = stock_amount + ?3 " +
            "WHERE user_id = ?1 AND stock_symbol = ?2 AND stock_cost IS NULL",
            nativeQuery = true)
    Integer incrementAmountBeforeSetCost(String userId, String stockSymbol, Integer stockAmount);

    @Transactional()
    @Modifying
    @Procedure(value = "increment_amount_after_set_cost")
    void incrementAmountAfterSetCost(
            @Param(value = "active_user") String userId,
            @Param(value = "ss")String stockSymbol,
            @Param(value = "amount_inc")Integer stockAmount);

    @Transactional
    @Modifying
    @Query(value =
            "UPDATE sell_trigger " +
            "SET stock_cost = ?2 " +
            "WHERE sell_trigger.user_id = ?1 AND sell_trigger.stock_symbol = ?3 AND sell_trigger.stock_cost IS NULL ",
            nativeQuery = true)
    Integer addCostAmount(String userId, Integer cost, String stockSymbol);
}
