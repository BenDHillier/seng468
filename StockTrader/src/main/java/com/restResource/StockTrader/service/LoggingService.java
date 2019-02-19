package com.restResource.StockTrader.service;

import com.restResource.StockTrader.entity.CommandType;
import com.restResource.StockTrader.entity.logging.*;
import com.restResource.StockTrader.repository.logging.*;
import org.apache.catalina.User;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlElement;
import java.io.File;
import java.io.FileWriter;
import java.io.StringWriter;

@Service
public class LoggingService {

    private LogXmlRepository logXmlRepository;
    private JAXBContext jaxbContext;

    public LoggingService(
                          LogXmlRepository logXmlRepository
    ) {
        this.logXmlRepository = logXmlRepository;
        try {
            this.jaxbContext = JAXBContext.newInstance(UserCommandLog.class,QuoteServerLog.class,SystemEventLog.class,ErrorEventLog.class,DebugEventLog.class);

        } catch( Exception e ) {
            e.printStackTrace();
        }

    }

    private Marshaller createMarshaller() throws JAXBException {
        Marshaller marshaller = this.jaxbContext.createMarshaller();
        // TODO: make this false which will save space in database
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
        return marshaller;
    }

    public void xmlLogEvent(LogXml log) {
        logXmlRepository.save(
                log.toBuilder().build());
    }

    public void logUserCommand(UserCommandLog log) {
        log.toBuilder()
                .server("TS1")
                .build();

        StringWriter writer = new StringWriter();

        try {
            Marshaller marshaller = createMarshaller();
            marshaller.marshal(log,writer);
            xmlLogEvent(
                    LogXml.builder()
                            .userId(log.getUsername())
                            .xmlLogEntry(writer.toString())
                            .build()
            );
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void logQuoteServer(QuoteServerLog log) {
//        QuoteServerLog quoteServerLog = new QuoteServerLog();
//        quoteServerLog.setPrice(price);
//        quoteServerLog.setStockSymbol(stockSymbol);
//        quoteServerLog.setQuoteServerTime(quoteServerTime);
//        quoteServerLog.setCryptokey(cryptokey);
//        quoteServerLog.setTimestamp(System.currentTimeMillis());
//        quoteServerLog.setServer("TS1");

        log.toBuilder()
                .server("TS1")
                .build();

        StringWriter writer = new StringWriter();

        try {
            Marshaller marshaller = createMarshaller();
            marshaller.marshal(log,writer);
            xmlLogEvent(
                    LogXml.builder()
                            .userId(log.getUsername())
                            .xmlLogEntry(writer.toString())
                            .build()
            );
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void logSystemEvent(SystemEventLog log) {

//        SystemEventLog systemEventLog = new SystemEventLog();
//        systemEventLog.setCommand(command);
//        systemEventLog.setUsername(username);
//        systemEventLog.setStockSymbol(stockSymbol);
//        systemEventLog.setFilename(filename);
//        systemEventLog.setFunds(funds);
//        systemEventLog.setServer("TS1");
//        systemEventLog.setTimestamp(System.currentTimeMillis());

        log.toBuilder()
                .server("TS1")
                .build();

        StringWriter writer = new StringWriter();

        try {
            Marshaller marshaller = createMarshaller();
            marshaller.marshal(log,writer);
            xmlLogEvent(
                    LogXml.builder()
                            .userId(log.getUsername())
                            .xmlLogEntry(writer.toString())
                            .build()
            );
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void logErrorEvent(ErrorEventLog log) {
//        ErrorEventLog errorEventLog = new ErrorEventLog();
//        errorEventLog.setTimestamp(System.currentTimeMillis());
//        errorEventLog.setServer("TS1");
//        errorEventLog.setCommand(command);
//        errorEventLog.setUserName(username);
//        errorEventLog.setStockSymbol(stockSymbol);
//        errorEventLog.setFileName(filename);
//        errorEventLog.setFunds(funds);
//        errorEventLog.setErrorMessage(errorMessage);
        log.toBuilder()
                .server("TS1")
                .build();

        StringWriter writer = new StringWriter();
        try {
            Marshaller marshaller = createMarshaller();
            marshaller.marshal(log,writer);
            xmlLogEvent(
                    LogXml.builder()
                            .userId(log.getUsername())
                            .xmlLogEntry(writer.toString())
                            .build()
            );
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void logDebugEvent(DebugEventLog log) {
//        DebugEventLog debugEventLog = new DebugEventLog();
//        debugEventLog.setTimestamp(System.currentTimeMillis());
//        debugEventLog.setServer("TS1");
//        debugEventLog.setCommand(command);
//        debugEventLog.setUserName(username);
//        debugEventLog.setStockSymbol(stockSymbol);
//        debugEventLog.setFileName(filename);
//        debugEventLog.setFunds(funds);
//        debugEventLog.setDebugMessage(debugMessage);
        log.toBuilder()
                .server("TS1")
                .build();
        StringWriter writer = new StringWriter();
        try {
            Marshaller marshaller = createMarshaller();
            marshaller.marshal(log,writer);
            xmlLogEvent(
                    LogXml.builder()
                            .userId(log.getUsername())
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
        }
        return null;
    }

    public File dumpUserLogToXmlFile(String filename, String userId) {
        try {
            Iterable<String> logFragments = logXmlRepository.findAllLogsForUser(userId);
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
        }
        return null;
    }
}
