package com.restResource.StockTrader.service;

import com.restResource.StockTrader.bean.UserCommandLogs;
import com.restResource.StockTrader.entity.logging.UserCommandLog;
import com.restResource.StockTrader.repository.logging.UserCommandLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//import java.util.Optional;

@Service
public class UserCommandLogToXMLService {

    @Autowired
    private UserCommandLogRepository userCommandLogRepository;

    public UserCommandLogs findAll() {
        Iterable<UserCommandLog> userCommandLogsList = userCommandLogRepository.findAll();
        UserCommandLogs userCommandLogs = new UserCommandLogs();
        userCommandLogs.setUserCommandLogList(userCommandLogsList);
        return userCommandLogs;
    }

//    public UserCommandLog findById(Integer id) {
//        UserCommandLog userCommandLog = userCommandLogRepository.findById(id);
//        return userCommandLog;
//    }
}
