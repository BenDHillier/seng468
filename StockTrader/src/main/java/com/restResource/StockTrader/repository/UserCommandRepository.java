package com.restResource.StockTrader.repository;

import com.restResource.StockTrader.entity.UserCommand;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;

public interface UserCommandRepository extends CrudRepository<UserCommand, Integer> {
    @Query(value =
            "INSERT INTO user_command VALUES (?1, ?2, ?3, ?4, ?5, ?6)",
            nativeQuery = true)
    void addUserCommandEntry(Long timestamp, String server, String transactionNum, String command, String username,  Integer funds);
}
