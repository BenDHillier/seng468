package com.restResource.StockTrader.service;

import com.google.common.collect.Lists;
import com.restResource.StockTrader.bean.EventLogs;
import com.restResource.StockTrader.entity.logging.EventLog;
import com.restResource.StockTrader.entity.logging.LogXml;
import com.restResource.StockTrader.entity.logging.UserCommandLog;
import com.restResource.StockTrader.repository.logging.EventLogRepository;
import com.restResource.StockTrader.repository.logging.LogXmlRepository;
//import com.restResource.StockTrader.repository.logging.UserCommandLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.util.ArrayList;
import java.util.List;

@Service
public class JaxbMarshallingService {

    @Autowired
    private EventLogRepository eventLogRepository;

    @Autowired
    private LogXmlRepository logXmlRepository;

    public JaxbMarshallingService(EventLogRepository eventLogRepository,
                                  LogXmlRepository logXmlRepository) {
        this.eventLogRepository = eventLogRepository;
        this.logXmlRepository = logXmlRepository;
    }

//    @Setter
//    @XmlElement(name="userCommand")
//    private List<EventLog> userCommandList = new ArrayList<>();
//
//    @Getter
//    @Setter
//    @XmlElement(name="quoteServer")
//    private List<EventLog> quoteServerLogList = new ArrayList<>();
//
//    @Getter
//    @Setter
//    @XmlElement(name="accountTransaction")
//    private List<EventLog> accountTransactionLogList = new ArrayList<>();
//
//    @Getter
//    @Setter
//    @XmlElement(name="systemEvent")
//    private List<EventLog> systemEventLogList = new ArrayList<>();
//
//    @Getter
//    @Setter
//    @XmlElement(name="errorEvent")
//    private List<EventLog> errorEventLogList = new ArrayList<>();
//
//    @Getter
//    @Setter
//    @XmlElement(name="debugEvent")
//    private List<EventLog> debugEventLogList = new ArrayList<>();

    public void dumpEventLogs() {
        try {
//            List<EventLog> userCommandLogList = Lists.newArrayList(eventLogRepository.findAllWithLogtype("userCommand"));
//            List<EventLog> quoteServerLogList = Lists.newArrayList(eventLogRepository.findAllWithLogtype("quoteServer"));
//            List<EventLog> accountTransactionLogList = Lists.newArrayList(eventLogRepository.findAllWithLogtype("accountTransaction"));
//            List<EventLog> systemEventLogList = Lists.newArrayList(eventLogRepository.findAllWithLogtype("systemEvent"));
//            List<EventLog> errorEventLogList = Lists.newArrayList(eventLogRepository.findAllWithLogtype("errorEvent"));
//            List<EventLog> debugEventLogList = Lists.newArrayList(eventLogRepository.findAllWithLogtype("debugEvent"));


//            List<EventLog> everyLog = Lists.newArrayList(eventLogRepository.findAll());
//            // TODO: try to merge all lists and sort by timestamp
//
//            EventLogs eventLogs = new EventLogs();
//            eventLogs.setEveryLogList(everyLog);
//            if(!accountTransactionLogList.isEmpty()) eventLogs.setAccountTransactionLogList(accountTransactionLogList);
//            if(!userCommandLogList.isEmpty()) eventLogs.setUserCommandList(userCommandLogList);
//            if(!quoteServerLogList.isEmpty()) eventLogs.setQuoteServerLogList(quoteServerLogList);
//            if(!systemEventLogList.isEmpty()) eventLogs.setSystemEventLogList(systemEventLogList);
//            if(!errorEventLogList.isEmpty()) eventLogs.setErrorEventLogList(errorEventLogList);
//            if(!debugEventLogList.isEmpty()) eventLogs.setDebugEventLogList(debugEventLogList);

//            JAXBContext jaxbContext = JAXBContext.newInstance(EventLogs.class,UserCommandLog.class);
//            Marshaller marshaller = jaxbContext.createMarshaller();
//            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
//            marshaller.marshal(eventLogs, System.out);
//            for( EventLog e : eventLogs.getEveryLogList() ) {
//                if(e.getLogtype().equals("userCommand")) {
//                    jaxbContext.
//                }
//                System.out.println( e.getLogtype() );
//            }

//            JAXBContext jaxbContext = JAXBContext.newInstance(EventLogs.class,UserCommandLog.class);
//            Marshaller marshaller = jaxbContext.createMarshaller();
//            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
//            marshaller.marshal(eventLogs, System.out);
            for(LogXml log : logXmlRepository.findAllLogs()) {
                System.out.println(log.toString());
            }

        } catch( Exception e ) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

}
