package com.restResource.StockTrader.repository;

import com.restResource.StockTrader.entity.Investment;
import com.restResource.StockTrader.entity.InvestmentId;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface InvestmentRepository extends CrudRepository<Investment, InvestmentId> {
    @Modifying
    @Transactional
    @Query(value =
            "INSERT INTO investment (owner, stock_symbol, stock_count) VALUES (?1, ?2, ?3) " +
            "ON CONFLICT (owner, stock_symbol) DO UPDATE " +
            "SET stock_count = investment.stock_count + ?3",
            nativeQuery = true)
    void insertOrIncrement(String owner, String stockSymbol, Integer amount);

    // Need separate method for decreaseing stockCount since the attempted insert
    // violates the minimum stockCount of 0 constraint.
    @Modifying
    @Transactional
    @Query(value = "UPDATE investment SET stock_count = stock_count - ?2 WHERE owner = ?1",
            nativeQuery = true)
    Integer removeStocks(String owner, Integer stockCount);

    @Query(value= "SELECT * FROM investment WHERE owner = ?1", nativeQuery = true)
    List<Investment> findByOwner(String userId);
}
