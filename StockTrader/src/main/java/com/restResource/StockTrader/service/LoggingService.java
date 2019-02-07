package com.restResource.StockTrader.service;

import com.restResource.StockTrader.entity.CommandType;
import com.restResource.StockTrader.entity.logging.*;
import com.restResource.StockTrader.repository.logging.*;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlElement;
import java.io.File;
import java.io.FileWriter;
import java.io.StringWriter;

@Service
public class LoggingService {

    private EventLogRepository eventLogRepository;
    private LogXmlRepository logXmlRepository;
    private JAXBContext jaxbContext;
    private Marshaller marshaller;

    public LoggingService(
                          LogXmlRepository logXmlRepository,
                          EventLogRepository eventLogRepository) {
        this.logXmlRepository = logXmlRepository;
        this.eventLogRepository = eventLogRepository;
        try {
            this.jaxbContext = JAXBContext.newInstance(UserCommandLog.class,QuoteServerLog.class,SystemEventLog.class,ErrorEventLog.class,DebugEventLog.class);
            this.marshaller = jaxbContext.createMarshaller();
            // TODO: make this false which will save space in database
            this.marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);
            this.marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
        } catch( Exception e ) {
            e.printStackTrace();
        }

    }

    public void logEvent(EventLog log) {
        eventLogRepository.save(
                log.toBuilder()
                        .timestamp(System.currentTimeMillis())
                        .server("TS1")
                        .build()
        );
    }

    public void xmlLogEvent(LogXml log) {
        logXmlRepository.save(
                log.toBuilder().build());
    }

//    public void altLogUserCommand(CommandType command, String username, String stockSymbol, String filename, Integer funds) {
//        logEvent(
//                EventLog.builder()
//                .command(command)
//                        .username(username)
//                        .stockSymbol(stockSymbol)
//                        .filename(filename)
//                        .funds(funds)
//                        .build()
//        );
//    }

    public void logUserCommand(CommandType command, String username, String stockSymbol, String filename, Integer funds) {
        UserCommandLog user = new UserCommandLog();
        user.setCommand(command);
        user.setUsername(username);
        user.setStockSymbol(stockSymbol);
        user.setFilename(filename);
        user.setFunds(funds);
        user.setTimestamp(System.currentTimeMillis());
        user.setServer("TS1");

        StringWriter writer = new StringWriter();

        try {
            marshaller.marshal(user,writer);
            xmlLogEvent(
                    LogXml.builder()
                            .userId(username)
                            .xmlLogEntry(writer.toString())
                            .build()
            );
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void logQuoteServer(Integer price, String stockSymbol, String username, Long quoteServerTime, String cryptokey) {
        QuoteServerLog quoteServerLog = new QuoteServerLog();
        quoteServerLog.setPrice(price);
        quoteServerLog.setStockSymbol(stockSymbol);
        quoteServerLog.setQuoteServerTime(quoteServerTime);
        quoteServerLog.setCryptokey(cryptokey);
        quoteServerLog.setTimestamp(System.currentTimeMillis());
        quoteServerLog.setServer("TS1");

        StringWriter writer = new StringWriter();

        try {
            marshaller.marshal(quoteServerLog,writer);
            xmlLogEvent(
                    LogXml.builder()
                            .userId(username)
                            .xmlLogEntry(writer.toString())
                            .build()
            );
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void logSystemEvent(CommandType command, String username, String stockSymbol, String filename, Integer funds) {

        SystemEventLog systemEventLog = new SystemEventLog();
        systemEventLog.setCommand(command);
        systemEventLog.setUsername(username);
        systemEventLog.setStockStymbol(stockSymbol);
        systemEventLog.setFilename(filename);
        systemEventLog.setFunds(funds);
        systemEventLog.setServer("TS1");
        systemEventLog.setTimestamp(System.currentTimeMillis());

        StringWriter writer = new StringWriter();

        try {
            marshaller.marshal(systemEventLog,writer);
            xmlLogEvent(
                    LogXml.builder()
                            .userId(username)
                            .xmlLogEntry(writer.toString())
                            .build()
            );
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void logErrorEvent(CommandType command, String username, String stockSymbol, String filename, Integer funds, String errorMessage) {
        ErrorEventLog errorEventLog = new ErrorEventLog();
        errorEventLog.setTimestamp(System.currentTimeMillis());
        errorEventLog.setServer("TS1");
        errorEventLog.setCommand(command);
        errorEventLog.setUserName(username);
        errorEventLog.setStockSymbol(stockSymbol);
        errorEventLog.setFileName(filename);
        errorEventLog.setFunds(funds);
        errorEventLog.setErrorMessage(errorMessage);
        StringWriter writer = new StringWriter();
        try {
            marshaller.marshal(errorEventLog,writer);
            xmlLogEvent(
                    LogXml.builder()
                            .userId(username)
                            .xmlLogEntry(writer.toString())
                            .build()
            );
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void logDebugEvent(CommandType command, String username, String stockSymbol, String filename, Integer funds, String debugMessage) {
        DebugEventLog debugEventLog = new DebugEventLog();
        debugEventLog.setTimestamp(System.currentTimeMillis());
        debugEventLog.setServer("TS1");
        debugEventLog.setCommand(command);
        debugEventLog.setUserName(username);
        debugEventLog.setStockSymbol(stockSymbol);
        debugEventLog.setFileName(filename);
        debugEventLog.setFunds(funds);
        debugEventLog.setDebugMessage(debugMessage);
        StringWriter writer = new StringWriter();
        try {
            marshaller.marshal(debugEventLog,writer);
            xmlLogEvent(
                    LogXml.builder()
                            .userId(username)
                            .xmlLogEntry(writer.toString())
                            .build()
            );
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public File dumpLogToXmlFile(String filename) {
        try {
            Iterable<String> logFragments = logXmlRepository.findAllLogs();
            FileWriter writer = new FileWriter("./"+filename);
            writer.write("<log>");
            for(String s : logFragments) {
                writer.write(s);
            }
            writer.write("</log>");
            File file = new File("./"+filename);
            writer.close();
            return file;

        } catch(Exception e) {
            System.out.println("Exception in LoggingService.dumpLogToXmlFile. See dumplog for more info");
            logErrorEvent(CommandType.DUMPLOG,null,null,filename,null,e.getMessage());
        }
        return null;
    }
}
