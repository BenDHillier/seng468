package com.restResource.StockTrader.service;

import com.restResource.StockTrader.entity.Quote;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//i think this shouldnt be a service but i dont know what to call it...
@Service
public class ThreadManager {

    private Map<String, Quote> tableOfHash;

    public ThreadManager(){
        this.tableOfHash = new ConcurrentHashMap<>();
    }

    public Map<String, Quote> getMap(){
        return tableOfHash;
    }

}
