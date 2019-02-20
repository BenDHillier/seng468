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

    private DatabaseNameResolver databaseNameResolver;
    private String timestamp = null;
    private String server = null;
    private String transactionNum = null;
    private String command = null;
    private String username = null;
    private String stockSymbol = null;
    private String filename = null;
    private String funds = null;


    protected static final Method GET_GENERATED_KEYS_METHOD;
    static {
        Method getGeneratedKeysMethod;
        try {
            // the
            getGeneratedKeysMethod = PreparedStatement.class.getMethod(
                    "getGeneratedKeys", (Class[]) null);
        } catch (Exception ex) {
            getGeneratedKeysMethod = null;
        }
        GET_GENERATED_KEYS_METHOD = getGeneratedKeysMethod;
    }


    public DatabaseAppender(){}

    @Override
    public void start() {

        super.start();
        if (databaseNameResolver == null)
            databaseNameResolver = new DatabaseNameResolver();
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
                timestamp = entries[0];
                server = entries[1];
                transactionNum = entries[2];
                command = entries[3];
                username = entries[4];
                stockSymbol = entries[5];
                filename = entries[6];
                funds = entries[7];
            }

            stmt.setString(1, timestamp);
            stmt.setString(2, server);
            stmt.setString(3, transactionNum);
            stmt.setString(4, command);
            stmt.setString(5, username);
            stmt.setString(6, stockSymbol);
            stmt.setString(7, filename);
            stmt.setString(8, funds);


        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            setNulls();
        }

    }

    public void setNulls() {
        timestamp = null;
        server = null;
        transactionNum = null;
        command = null;
        username = null;
        stockSymbol = null;
        filename = null;
        funds = null;
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