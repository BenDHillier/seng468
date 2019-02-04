package com.restResource.StockTrader.service;

import com.google.common.collect.Lists;
import com.restResource.StockTrader.bean.EventLogs;
import com.restResource.StockTrader.entity.logging.EventLog;
import com.restResource.StockTrader.repository.logging.EventLogRepository;
import com.restResource.StockTrader.repository.logging.UserCommandLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.util.List;

@Service
public class JaxbMarshallingService {

    @Autowired
    private EventLogRepository eventLogRepository;

    public JaxbMarshallingService(EventLogRepository eventLogRepository) {
        this.eventLogRepository = eventLogRepository;
    }

    public void dumpEventLogs() {
        try {
            List<EventLog> accountTransactionLogList = Lists.newArrayList(eventLogRepository.findAllWithLogtype("accountTransaction"));
            List<EventLog> userCommandLogList = Lists.newArrayList(eventLogRepository.findAllWithLogtype("userCommand"));

            EventLogs eventLogs = new EventLogs();
            if(!accountTransactionLogList.isEmpty()) eventLogs.setAccountTransactionLogList(accountTransactionLogList);
            if(!userCommandLogList.isEmpty()) eventLogs.setUserCommandList(userCommandLogList);


            JAXBContext jaxbContext = JAXBContext.newInstance(EventLogs.class);
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(eventLogs, System.out);

        } catch( Exception e ) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

}
