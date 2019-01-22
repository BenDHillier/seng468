package com.restResource.StockTrader.repository;

import com.restResource.StockTrader.entity.Account;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

public interface AccountRepository extends CrudRepository<Account, Integer> {
    @Modifying
    @Transactional
    @Query(value =
            "INSERT INTO account VALUES (?1, ?2) " +
                    "ON CONFLICT (user_id) DO UPDATE " +
                    "SET amount = account.amount + ?2 ",
        nativeQuery = true)
    void updateAccountBalance(String userId, Integer amount);
}


//INSERT INTO account VALUES('alex01', 22) ON CONFLICT (user_id) DO UPDATE SET amount = account.amount + 1 WHERE account.user_id = 'alex01';

//public interface InvestmentRepository extends CrudRepository<Investment, InvestmentId> {
//    @Modifying
//    @Transactional
//    @Query(value =
//            "INSERT INTO investment VALUES (?1, ?2, ?3) " +
//                    "ON CONFLICT (owner, stock_symbol) DO UPDATE " +
//                    "SET amount = investment.amount + ?3",
//            nativeQuery = true)
//    void insertOrIncrement(String owner, String stockSymbol, Integer amount);
//}