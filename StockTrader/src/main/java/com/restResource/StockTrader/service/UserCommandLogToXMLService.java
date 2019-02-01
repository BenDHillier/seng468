package com.restResource.StockTrader.service;

import com.restResource.StockTrader.bean.UserCommandLogs;
import com.restResource.StockTrader.entity.logging.UserCommandLog;
import com.restResource.StockTrader.repository.logging.UserCommandLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserCommandLogService {

    @Autowired
    private UserCommandLogRepository userCommandLogRepository;

    public UserCommandLogs findAll() {
        Iterable<UserCommandLog> userCommandLog = userCommandLogRepository.findAll();
        return 
    }
}
