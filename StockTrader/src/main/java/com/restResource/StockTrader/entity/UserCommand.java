package com.restResource.StockTrader.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;

/**
 * Class for a UserCommand table entry.
 */
@Builder(toBuilder = true)
@Entity
@Table(name = "userCommand")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCommand {
    @Min(value = 0)
    Long timestamp;
    String server;
    String transactionNum;
    String command;
    String username;
    Integer funds;
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    Integer id;

}