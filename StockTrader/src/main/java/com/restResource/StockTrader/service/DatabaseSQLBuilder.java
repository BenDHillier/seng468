package com.restResource.StockTrader.service;

public class DatabaseSQLBuilder {

    static String buildInsertSQL() {

        StringBuilder sqlBuilder = new StringBuilder("INSERT INTO ");
        sqlBuilder.append("\"userCommand\"").append(" (");
	    sqlBuilder.append("timestamp").append(", ");
        sqlBuilder.append("server").append(", ");
        sqlBuilder.append("\"transactionNum\"").append(", ");
        sqlBuilder.append("command").append(", ");
        sqlBuilder.append("username").append(", ");
        sqlBuilder.append("\"stockSymbol\"").append(", ");
        sqlBuilder.append("filename").append(", ");
        sqlBuilder.append("funds").append(") ");
        sqlBuilder.append("VALUES (?,?,?,?,?,?,?,?)");
        return sqlBuilder.toString();
    }

}