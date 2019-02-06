package com.restResource.StockTrader.service;

import com.restResource.StockTrader.bean.UserCommandLogs;
import org.springframework.stereotype.Service;


@Service
public class UserCommandLogToXMLService {



    public UserCommandLogs findAll() {


        UserCommandLogs userCommandLogs = new UserCommandLogs();
        return userCommandLogs;
    }
}
