package com.restResource.StockTrader.service;

import ch.qos.logback.classic.db.DBAppender;
import ch.qos.logback.classic.spi.ILoggingEvent;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

public class DatabaseAppender extends DBAppender {
    protected String insertSQL;

    private String command = null;
    private String timestamp = null;
    private String quoteServerTime = null;
    private String server = null;
    private String transactionNum = null;
    private String username = null;
    private String stockSymbol = null;
    private String filename = null;
    private String funds = null;
    private String price = null;
    private String cryptokey = null;
    private String errorMessage = null;
    private String debugMessage = null;
    private String logtype = null;
    private String action = null;


    protected static final Method GET_GENERATED_KEYS_METHOD;
    static {
        Method getGeneratedKeysMethod;
        try {
            getGeneratedKeysMethod = PreparedStatement.class.getMethod(
                    "getGeneratedKeys", (Class[]) null);
        } catch (Exception ex) {
            getGeneratedKeysMethod = null;
        }
        GET_GENERATED_KEYS_METHOD = getGeneratedKeysMethod;
    }


    public DatabaseAppender(){}

    //This method is called only once on system init
    @Override
    public void start() {
        super.start();
//        if (databaseNameResolver == null)
//            databaseNameResolver = new DatabaseNameResolver();
        insertSQL = DatabaseSQLBuilder.buildInsertSQL();
        //insertSQL = DatabaseSQLBuilder.buildInsertSQL(databaseNameResolver);

    }

    @Override
    protected void subAppend(ILoggingEvent event, Connection connection,
                             PreparedStatement insertStatement) throws Throwable {

        bindLoggingEventWithInsertStatement(insertStatement, event);

        int updateCount = -1;
        try {
            updateCount = insertStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(" updated = "+ updateCount + " rows");
    }


    private void bindLoggingEventWithInsertStatement(PreparedStatement stmt, ILoggingEvent event) throws SQLException {
        String logdata = event.getFormattedMessage();
        System.out.println(logdata);
        parseLogData(logdata, stmt);
    }

    private void parseLogData(String logdata, PreparedStatement stmt) throws SQLException {
        setNulls();
        try {
            String[] entries = logdata.split(",");
            if (entries.length > 0) {
                command = entries[0];
                timestamp = entries[1];
                quoteServerTime = entries[2];
                server = entries[3];
                transactionNum = entries[4];
                username = entries[5];
                stockSymbol = entries[6];
                filename = entries[7];
                funds = entries[8];
                price = entries[9];
                cryptokey = entries[10];
                errorMessage = entries[11];
                debugMessage = entries[12];
                logtype = entries[13];
                action = entries[14];
            }

            stmt.setString(1, command);
            stmt.setString(2, timestamp);
            stmt.setString(3, quoteServerTime);
            stmt.setString(4, server);
            stmt.setString(5, transactionNum);
            stmt.setString(6, username);
            stmt.setString(7, stockSymbol);
            stmt.setString(8, filename);
            stmt.setString(9, funds);
            stmt.setString(10, price);
            stmt.setString(11, cryptokey);
            stmt.setString(12, errorMessage);
            stmt.setString(13, debugMessage);
            stmt.setString(14, logtype);
            stmt.setString(15, action);


        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void setNulls() {
        command = null;
        timestamp = null;
        quoteServerTime = null;
        server = null;
        transactionNum = null;
        username = null;
        stockSymbol = null;
        filename = null;
        funds = null;
        price = null;
        cryptokey = null;
        errorMessage = null;
        debugMessage = null;
        logtype = null;
        action = null;
    }

    @Override
    protected Method getGeneratedKeysMethod() {
        return GET_GENERATED_KEYS_METHOD;
    }

    @Override
    protected String getInsertSQL() {
        return insertSQL;
    }

    protected void insertProperties(Map<String, String> mergedMap,
                                    Connection connection, long eventId) throws SQLException {
    }
}