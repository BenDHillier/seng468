package com.restResource.StockTrader.repository;

import com.restResource.StockTrader.entity.Account;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

public interface AccountRepository extends CrudRepository<Account, String> {
    @Modifying
    @Transactional
    @Query(value =
            "INSERT INTO account VALUES (?1, ?2, ?3, ?4, ?5) " +
                    "ON CONFLICT (user_id) DO UPDATE " +
                    "SET amount = account.amount + ?2, " +
                    "last_transaction_number = ?3, " +
                    "last_server = ?4, " +
                    "last_transaction_time = ?5",
        nativeQuery = true)
    void updateAccountBalance(String userId, Integer amount, Integer transactionNum, String server, Long time);


    // Need separate method for removing funds since the attempted insert
    // violates the minimum amount of 0 constraint.
    @Modifying
    @Transactional
    @Query(value = "UPDATE account SET amount = amount - ?2, " +
                   "last_transaction_number = ?3, " +
                   "last_server = ?4, " +
                   "last_transaction_time = ?5 " +
                   "WHERE user_id = ?1",
            nativeQuery = true)
    Integer removeFunds(String userId, Integer amount, Integer transactionNum, String server, Long time);

    //Need to check if user account exists to prevent unnecessary quoteServer requests in BuyController
    @Query(value = "SELECT EXISTS( SELECT 1 FROM account WHERE user_id = ?1)", nativeQuery = true)
    Boolean accountExists(String userId);

}