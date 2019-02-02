package com.restResource.StockTrader.service;

import com.restResource.StockTrader.bean.UserCommandLogs;
import com.restResource.StockTrader.entity.logging.AccountTransactionLog;
import com.restResource.StockTrader.entity.logging.ErrorEventLog;
import com.restResource.StockTrader.entity.logging.QuoteServerLog;
import com.restResource.StockTrader.entity.logging.UserCommandLog;
import com.restResource.StockTrader.repository.logging.AccountTransactionLogRepository;
import com.restResource.StockTrader.repository.logging.ErrorEventLogRepository;
import com.restResource.StockTrader.repository.logging.QuoteServerLogRepository;
import com.restResource.StockTrader.repository.logging.UserCommandLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//import java.util.Optional;

@Service
public class UserCommandLogToXMLService {

    @Autowired
    private UserCommandLogRepository userCommandLogRepository;
    @Autowired
    private AccountTransactionLogRepository accountTransactionLogRepository;
    @Autowired
    private QuoteServerLogRepository quoteServerLogRepository;
    @Autowired
    private ErrorEventLogRepository errorEventLogRepository;

    public UserCommandLogs findAll() {
        Iterable<UserCommandLog> userCommandLogsList = userCommandLogRepository.findAll();
        Iterable<AccountTransactionLog> accountTransactionsLogsList = accountTransactionLogRepository.findAll();
        Iterable<QuoteServerLog> quoteServerTransactionLogsList = quoteServerLogRepository.findAll();
        Iterable<ErrorEventLog> errorEventLogsList = errorEventLogRepository.findAll();

        UserCommandLogs userCommandLogs = new UserCommandLogs();
        userCommandLogs.setUserCommandLogList(userCommandLogsList);
        userCommandLogs.setAccountTransactionLogList(accountTransactionsLogsList);
        userCommandLogs.setQuoteServerTransactionLogList(quoteServerTransactionLogsList);
        userCommandLogs.setErrorEventLogList(errorEventLogsList);


        return userCommandLogs;
    }


//    public UserCommandLog findById(Integer id) {
//        UserCommandLog userCommandLog = userCommandLogRepository.findById(id);
//        return userCommandLog;
//    }
}
