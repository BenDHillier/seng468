package com.restResource.StockTrader.controller;
import com.restResource.StockTrader.bean.UserCommandLogs;
import com.restResource.StockTrader.entity.CommandType;
import com.restResource.StockTrader.entity.logging.ErrorEventLog;
import com.restResource.StockTrader.entity.logging.EventLog;
import com.restResource.StockTrader.entity.logging.UserCommandLog;
import com.restResource.StockTrader.repository.AccountRepository;
import com.restResource.StockTrader.service.JaxbMarshallingService;
import com.restResource.StockTrader.service.LoggingService;
import com.restResource.StockTrader.service.UserCommandLogToXMLService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AddController {

    private AccountRepository accountRepository;

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

    loggingService.logEvent(
            EventLog.builder()
                    .logtype("userCommand")
                    .username(userId)
                    .funds(amount)
                    .command(CommandType.ADD)
                    .build());

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
            loggingService.logEvent(
                    EventLog.builder()
                            .logtype("errorEvent")
                            .command(CommandType.ADD)
                            .username(userId)
                            .funds(amount)
                            .errorMessage("Amount added must be <= 0")
                            .build());
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

    @RequestMapping(value = "/dumplog")
    public void printAllLogs() {
        System.out.println("Attempting to print logs based on call to \"printAllLogs()\"");
        jaxbMarshallingService.dumpEventLogs();
    }
}