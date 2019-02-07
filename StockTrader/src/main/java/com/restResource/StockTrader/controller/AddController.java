package com.restResource.StockTrader.controller;
import com.restResource.StockTrader.bean.UserCommandLogs;
import com.restResource.StockTrader.entity.CommandType;
import com.restResource.StockTrader.entity.logging.ErrorEventLog;
import com.restResource.StockTrader.entity.logging.EventLog;
import com.restResource.StockTrader.entity.logging.UserCommandLog;
import com.restResource.StockTrader.repository.AccountRepository;
//import com.restResource.StockTrader.repository.logging.UserCommandLogRepository;
import com.restResource.StockTrader.service.JaxbMarshallingService;
import com.restResource.StockTrader.service.LoggingService;
import com.restResource.StockTrader.service.UserCommandLogToXMLService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.xml.ws.Response;
import java.io.File;
import java.io.FileInputStream;

@RestController
public class AddController {

    private AccountRepository accountRepository;

    //private UserCommandLogRepository userCommandLogRepository;

    private LoggingService loggingService;

    //private UserCommandLogToXMLService userCommandLogToXMLService;
    private JaxbMarshallingService jaxbMarshallingService;

    public AddController(AccountRepository accountRepository, LoggingService loggingService, JaxbMarshallingService jaxbMarshallingService) {
        this.accountRepository = accountRepository;
        this.loggingService = loggingService;
        //this.userCommandLogToXMLService = userCommandLogToXMLService;
        this.jaxbMarshallingService = jaxbMarshallingService;
    }

    @PutMapping(value = "/add")
    public @ResponseBody
    HttpStatus addToAccountBalance(@RequestParam String userId,
                                   @RequestParam int amount) {

        loggingService.logUserCommand(CommandType.ADD, userId, null, null, amount);
        loggingService.logDebugEvent(CommandType.ADD,userId,null,null,amount,"Trying to add funds to this guys account");
        //loggingService.altLogUserCommand(CommandType.ADD,userId,null,null,amount);

        try {
            if (amount <= 0) {
                throw new IllegalArgumentException(
                        "The ADD amount parameter must be greater than zero");
            } else {
                accountRepository.updateAccountBalance(userId, amount);
            }
            return HttpStatus.OK;
        } catch (Exception e) {
            System.out.println("Exception in AddController: " + e.toString());
            loggingService.logErrorEvent(CommandType.ADD,userId,null,null,amount,"Amount added must be lteq 0");
            return HttpStatus.BAD_REQUEST;
        }
    }

//    // TODO: move dumplog into its own controller
//    @RequestMapping(value = "/dumplog", produces = MediaType.APPLICATION_XML_VALUE)
//    public @ResponseBody
//    ResponseEntity<UserCommandLogs> findUserCommandLogs(@RequestParam String filename) {
//        loggingService.logUserCommand(
//                UserCommandLog.builder()
//                        .command(CommandType.DUMPLOG)
//                        .username("NULL")
//                        .filename(filename)
//                        .build());
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
//        headers.add("Pragma", "no-cache");
//        headers.add("Expires", "0");
//        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
//        return ResponseEntity.ok()
//                .headers(headers)
//                .contentType(MediaType.APPLICATION_XML)
//                .body(userCommandLogToXMLService.findAll());
//    }

    @RequestMapping(value = "/printlogs")
    public void printAllLogs() {
        System.out.println("Attempting to print logs based on call to \"printAllLogs()\"");
        loggingService.dumpLogToXmlFile("./logs.xml");
    }

    @RequestMapping(value="/dumplog")
    public ResponseEntity<Resource> dumpLogs(@RequestParam String filename) {
        try {
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
            e.printStackTrace();
        }
        return null;
    }
}