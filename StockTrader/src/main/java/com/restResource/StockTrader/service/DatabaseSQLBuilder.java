package com.restResource.StockTrader.service;

public class DatabaseSQLBuilder {

    static String buildInsertSQL() {




        StringBuilder sqlBuilder = new StringBuilder("INSERT INTO ");
        sqlBuilder.append("logs").append("( ");
        sqlBuilder.append("command").append(", ");
	    sqlBuilder.append("timestamp").append(", ");
        sqlBuilder.append("quote_server_time").append(", ");
        sqlBuilder.append("server").append(", ");
        sqlBuilder.append("transaction_num").append(", ");
        sqlBuilder.append("username").append(", ");
        sqlBuilder.append("stock_symbol").append(", ");
        sqlBuilder.append("filename").append(", ");
        sqlBuilder.append("funds").append(", ");
        sqlBuilder.append("price").append(", ");
        sqlBuilder.append("cryptokey").append(", ");
        sqlBuilder.append("error_message").append(", ");
        sqlBuilder.append("debug_message").append(", ");
        sqlBuilder.append("logtype").append(", ");
        sqlBuilder.append("action").append(") ");
        sqlBuilder.append("VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
        return sqlBuilder.toString();
    }

}