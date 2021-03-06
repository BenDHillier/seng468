package com.restResource.StockTrader.controller;

import com.restResource.StockTrader.entity.CommandType;
import com.restResource.StockTrader.entity.logging.UserCommandLog;
import com.restResource.StockTrader.service.LoggingService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;

@RestController
public class LogController {
    private LoggingService loggingService;

    public LogController(LoggingService loggingService) {
        this.loggingService = loggingService;
    }

    @RequestMapping(value="/dumplog/all")
    public ResponseEntity<Resource> dumpLogs(@RequestParam String filename,
                                             @RequestParam int transactionNum) {
        try {
            loggingService.logUserCommand(CommandType.DUMPLOG.toString(), Long.toString(System.currentTimeMillis()),"TS1",Integer.toString(transactionNum),"NULL","NULL",filename,"NULL");
            loggingService.dumpLogToXmlFile(filename);
            File f = new File(filename);
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");
            InputStreamResource resource;
            resource = new InputStreamResource(new FileInputStream(f));
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_XML)
                    .contentLength(f.length())
                    .body(resource);
        } catch(Exception e) {
            System.out.println("Exception in LogController: " + e.getMessage());
        }
        return null;
    }

    @RequestMapping(value="/dumplog/user")
    public ResponseEntity<Resource> dumpUserLogs(@RequestParam String filename,
                                                 @RequestParam String userId,
                                                 @RequestParam int transactionNum) {
        try {
            loggingService.logUserCommand(CommandType.DUMPLOG.toString(), Long.toString(System.currentTimeMillis()),"TS1",Integer.toString(transactionNum),userId,"NULL",filename,"NULL");
            loggingService.dumpUserLogToXmlFile(filename,userId);
            File f = new File(filename);
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");
            InputStreamResource resource;
            resource = new InputStreamResource(new FileInputStream(f));
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_XML)
                    .contentLength(f.length())
                    .body(resource);
        } catch(Exception e) {
            System.out.println("Exception in LogController: " + e.getMessage());
        }
        return null;
    }
}
