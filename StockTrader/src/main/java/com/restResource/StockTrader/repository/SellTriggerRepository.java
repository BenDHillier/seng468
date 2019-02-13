package com.restResource.StockTrader.repository;

import com.restResource.StockTrader.entity.BuyTrigger;
import com.restResource.StockTrader.entity.SellTrigger;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface SellTriggerRepository extends CrudRepository<SellTrigger, String> {
    @Query(value=
            "Select * from buy_trigger where user_id = ?1 and stock_symbol=?2 ",
            nativeQuery = true)
    Optional<SellTrigger> findByUserIdAndStockSymbol(String userId, String stockSymbol);
}
