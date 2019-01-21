package com.restResource.StockTrader.repository;

import com.restResource.StockTrader.entity.Investment;
import com.restResource.StockTrader.entity.InvestmentId;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

public interface InvestmentRepository extends CrudRepository<Investment, InvestmentId> {
    @Modifying
    @Transactional
    @Query(value =
            "INSERT INTO investment VALUES (?1, ?2, ?3) " +
            "ON CONFLICT (owner, stock_symbol) DO UPDATE " +
            "SET amount = investment.amount + ?3",
            nativeQuery = true)
    void insertOrIncrement(String owner, String stockSymbol, Integer amount);
}
