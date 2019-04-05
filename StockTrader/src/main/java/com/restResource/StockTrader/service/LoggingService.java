package com.restResource.StockTrader.service;

import com.restResource.StockTrader.entity.logging.*;
import com.restResource.StockTrader.repository.logging.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;

@Service
public class LoggingService {

    private LogsRepository logsRepo;
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    protected Marker logMarker = MarkerFactory.getMarker("logMarker");

    public LoggingService(LogsRepository logsRepo) {
        this.logsRepo = logsRepo;
    }

    public void logUserCommand(String command, String timestamp, String server, String transactionNum, String username, String stockSymbol, String filename, String funds) {
        StringBuilder logBuilder = new StringBuilder();
        logBuilder.append(command)//command
                .append(",")
                .append(timestamp)//timestamp
                .append(",")
                .append("NULL")//quoteServerTime
                .append(",")
                .append(server)//server
                .append(",")
                .append(transactionNum)//transactionNum
                .append(",")
                .append(username)//username
                .append(",")
                .append(stockSymbol)//stocksymbol
                .append(",")
                .append(filename)//filename
                .append(",")
                .append(funds)//funds
                .append(",")
                .append("NULL")//price
                .append(",")
                .append("NULL")//cryptokey
                .append(",")
                .append("NULL")//errorMessage
                .append(",")
                .append("NULL")//debugMessage
                .append(",")
                .append("UserCommandType")//logtype
                .append(",")
                .append("NULL");//action
        logger.debug(logMarker,logBuilder.toString());
    }

    public void logQuoteServer(String timestamp, String server, String transactionNum, String price, String stockSymbol, String username, String quoteServerTime, String cryptokey) {
        StringBuilder logBuilder = new StringBuilder();
        logBuilder.append("NULL")//command
                .append(",")
                .append(timestamp)//timestamp
                .append(",")
                .append(quoteServerTime)//quoteServerTime
                .append(",")
                .append(server)//server
                .append(",")
                .append(transactionNum)//transactionNum
                .append(",")
                .append(username)//username
                .append(",")
                .append(stockSymbol)//stocksymbol
                .append(",")
                .append("NULL")//filename
                .append(",")
                .append("NULL")//funds
                .append(",")
                .append(price)//price
                .append(",")
                .append(cryptokey)//cryptokey
                .append(",")
                .append("NULL")//errorMessage
                .append(",")
                .append("NULL")//debugMessage
                .append(",")
                .append("QuoteServerType")//logtype
                .append(",")
                .append("NULL");//action
        logger.debug(logMarker,logBuilder.toString());
    }

    public void logSystemEvent(String timestamp, String server, String transactionNum, String command, String username, String stockSymbol, String filename, String funds) {
        StringBuilder logBuilder = new StringBuilder();
        logBuilder.append(command)//command
                .append(",")
                .append(timestamp)//timestamp
                .append(",")
                .append("NULL")//quoteServerTime
                .append(",")
                .append(server)//server
                .append(",")
                .append(transactionNum)//transactionNum
                .append(",")
                .append(username)//username
                .append(",")
                .append(stockSymbol)//stocksymbol
                .append(",")
                .append(filename)//filename
                .append(",")
                .append(funds)//funds
                .append(",")
                .append("NULL")//price
                .append(",")
                .append("NULL")//cryptokey
                .append(",")
                .append("NULL")//errorMessage
                .append(",")
                .append("NULL")//debugMessage
                .append(",")
                .append("SystemEventType")//logtype
                .append(",")
                .append("NULL");//action
        logger.debug(logMarker,logBuilder.toString());
    }

    public void logErrorEvent(ErrorEventLog log) {
        System.out.println("Tried to log an error event but its not implemented yet...");
    }

    public void logDebugEvent(DebugEventLog log) {
    }

    public File dumpLogToXmlFile(String filename) {
        try {
            Iterable<CentralLog> logFrags = logsRepo.findAllLogs();
            FileWriter writer = new FileWriter("./"+filename);
            writer.write("<log>");
            for(CentralLog s : logFrags) {
                if( s.getLogtype().equals("UserCommandType") ) {

                    String usernameString = s.getUsername();
                    String usernameTag = "";
                    if( !usernameString.equals("NULL") ) usernameTag = "<username>" + usernameString + "</username>";

                    String stockSymbolString = s.getStock_symbol();
                    String stockSymbolTag = "";
                    if( !stockSymbolString.equals("NULL") ) stockSymbolTag = "<stockSymbol>" + stockSymbolString + "</stockSymbol>";

                    String filenameString = s.getFilename();
                    String filenameTag = "";
                    if( !filenameString.equals("NULL") ) filenameTag = "<filename>" + filenameString + "</filename>";

                    String fundsString = s.getFunds();
                    String fundsTag = "";
                    if( !fundsString.equals("NULL") ) fundsTag = "<funds>" + fundsString + "</funds>";

                    writer.write("<userCommand>"+
                            "<command>" + s.getCommand() + "</command>" +
                            "<timestamp>" + s.getTimestamp() + "</timestamp>" +
                            "<server>" + s.getServer() + "</server>" +
                            "<transactionNum>" + s.getTransaction_num() + "</transactionNum>" +
                            usernameTag +
                            stockSymbolTag +
                            filenameTag +
                            fundsTag+
                            "</userCommand>");
                }
                else if( s.getLogtype().equals("QuoteServerType")) {
                    writer.write(
                            "<quoteServer>" +
                            "<timestamp>" + s.getTimestamp() + "</timestamp>" +
                            "<server>" + s.getServer() + "</server>" +
                            "<transactionNum>" + s.getTransaction_num() + "</transactionNum>" +
                            "<price>" + s.getPrice() + "</price>" +
                            "<stockSymbol>" + s.getStock_symbol() + "</stockSymbol>" +
                            "<username>" + s.getUsername() + "</username>" +
                            "<quoteServerTime>" + s.getQuote_server_time() + "</quoteServerTime>" +
                            "<cryptokey>" + s.getCryptokey() + "</cryptokey>" +
                            "</quoteServer>");
                }
                else if( s.getLogtype().equals("SystemEventType")) {
                    String usernameString = s.getUsername();
                    String usernameTag = "";
                    if( !usernameString.equals("NULL") ) usernameTag = "<username>" + usernameString + "</username>";

                    String stockSymbolString = s.getStock_symbol();
                    String stockSymbolTag = "";
                    if( !stockSymbolString.equals("NULL") ) stockSymbolTag = "<stockSymbol>" + stockSymbolString + "</stockSymbol>";

                    String filenameString = s.getFilename();
                    String filenameTag = "";
                    if( !filenameString.equals("NULL") ) filenameTag = "<filename>" + filenameString + "</filename>";

                    String fundsString = s.getFunds();
                    String fundsTag = "";
                    if( !fundsString.equals("NULL") ) fundsTag = "<funds>" + fundsString + "</funds>";

                    writer.write(
                            "<systemEvent>" +
                            "<timestamp>" + s.getTimestamp() + "</timestamp>" +
                            "<server>" + s.getServer() + "</server>" +
                            "<transactionNum>" + s.getTransaction_num() + "</transactionNum>" +
                            "<command>" + s.getCommand() + "</command>" +
                            usernameTag +
                            stockSymbolTag +
                            filenameTag +
                            fundsTag +
                            "</systemEvent>"
                    );
                }
                else if( s.getLogtype().equals("AccountTransactionType")) {
                    writer.write(
                            "<accountTransaction>" +
                            "<timestamp>" + s.getTimestamp() + "</timestamp>" +
                            "<server>" + s.getServer() + "</server>" +
                            "<transactionNum>" + s.getTransaction_num() + "</transactionNum>" +
                            "<action>" + s.getAction() + "</action>" +
                            "<username>" + s.getUsername() + "</username>" +
                            "<funds>" + s.getFunds() + "</funds>" +
                            "</accountTransaction>"
                    );
                }
            }
            writer.write("</log>");
            writer.close();
            File file = new File("./"+filename);
            return file;
        } catch( Exception e ) {
            System.out.println("Exception in dumpLogToXmlFile: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public File dumpUserLogToXmlFile(String filename, String userId) {
    try {
        Iterable<CentralLog> logFrags = logsRepo.findAllLogsForUser(userId);
        FileWriter writer = new FileWriter("./"+filename);
        writer.write("<log>");
        for(CentralLog s : logFrags) {
            if( s.getLogtype().equals("UserCommandType") ) {

                String usernameString = s.getUsername();
                String usernameTag = "";
                if( !usernameString.equals("NULL") ) usernameTag = "<username>" + usernameString + "</username>";

                String stockSymbolString = s.getStock_symbol();
                String stockSymbolTag = "";
                if( !stockSymbolString.equals("NULL") ) stockSymbolTag = "<stockSymbol>" + stockSymbolString + "</stockSymbol>";

                String filenameString = s.getFilename();
                String filenameTag = "";
                if( !filenameString.equals("NULL") ) filenameTag = "<filename>" + filenameString + "</filename>";

                String fundsString = s.getFunds();
                String fundsTag = "";
                if( !fundsString.equals("NULL") ) fundsTag = "<funds>" + fundsString + "</funds>";

                writer.write("<userCommand>"+
                        "<command>" + s.getCommand() + "</command>" +
                        "<timestamp>" + s.getTimestamp() + "</timestamp>" +
                        "<server>" + s.getServer() + "</server>" +
                        "<transactionNum>" + s.getTransaction_num() + "</transactionNum>" +
                        usernameTag +
                        stockSymbolTag +
                        filenameTag +
                        fundsTag+
                        "</userCommand>");
            }
            else if( s.getLogtype().equals("QuoteServerType")) {
                writer.write(
                        "<quoteServer>" +
                                "<timestamp>" + s.getTimestamp() + "</timestamp>" +
                                "<server>" + s.getServer() + "</server>" +
                                "<transactionNum>" + s.getTransaction_num() + "</transactionNum>" +
                                "<price>" + s.getPrice() + "</price>" +
                                "<stockSymbol>" + s.getStock_symbol() + "</stockSymbol>" +
                                "<username>" + s.getUsername() + "</username>" +
                                "<quoteServerTime>" + s.getQuote_server_time() + "</quoteServerTime>" +
                                "<cryptokey>" + s.getCryptokey() + "</cryptokey>" +
                                "</quoteServer>");
            }
            else if( s.getLogtype().equals("SystemEventType")) {
                String usernameString = s.getUsername();
                String usernameTag = "";
                if( !usernameString.equals("NULL") ) usernameTag = "<username>" + usernameString + "</username>";

                String stockSymbolString = s.getStock_symbol();
                String stockSymbolTag = "";
                if( !stockSymbolString.equals("NULL") ) stockSymbolTag = "<stockSymbol>" + stockSymbolString + "</stockSymbol>";

                String filenameString = s.getFilename();
                String filenameTag = "";
                if( !filenameString.equals("NULL") ) filenameTag = "<filename>" + filenameString + "</filename>";

                String fundsString = s.getFunds();
                String fundsTag = "";
                if( !fundsString.equals("NULL") ) fundsTag = "<funds>" + fundsString + "</funds>";

                writer.write(
                        "<systemEvent>" +
                                "<timestamp>" + s.getTimestamp() + "</timestamp>" +
                                "<server>" + s.getServer() + "</server>" +
                                "<transactionNum>" + s.getTransaction_num() + "</transactionNum>" +
                                "<command>" + s.getCommand() + "</command>" +
                                usernameTag +
                                stockSymbolTag +
                                filenameTag +
                                fundsTag +
                                "</systemEvent>"
                );
            }
            else if( s.getLogtype().equals("AccountTransactionType")) {
                writer.write(
                        "<accountTransaction>" +
                                "<timestamp>" + s.getTimestamp() + "</timestamp>" +
                                "<server>" + s.getServer() + "</server>" +
                                "<transactionNum>" + s.getTransaction_num() + "</transactionNum>" +
                                "<action>" + s.getAction() + "</action>" +
                                "<username>" + s.getUsername() + "</username>" +
                                "<funds>" + s.getFunds() + "</funds>" +
                                "</accountTransaction>"
                );
            }
        }
        writer.write("</log>");
        writer.close();
        File file = new File("./"+filename);
        return file;
    } catch( Exception e ) {
        System.out.println("Exception in dumpLogToXmlFile: " + e.getMessage());
        e.printStackTrace();
    }
    return null;
}
}

