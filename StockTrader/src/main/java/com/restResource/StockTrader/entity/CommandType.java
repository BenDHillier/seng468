package com.restResource.StockTrader.entity;

public enum CommandType {
    ADD,
    BUY,
    SET_BUY_AMOUNT,
    SET_SELL_AMOUNT,
    SET_BUY_TRIGGER,
    SET_SELL_TRIGGER,
    CANCEL_SET_BUY,
    CANCEL_SET_SELL,
    SELL,
    COMMIT_BUY,
    CANCEL_BUY,
    COMMIT_SELL,
    CANCEL_SELL,
    QUOTE,
    DUMPLOG,
    DISPLAY_SUMMARY,
    NONE
}
