package com.restResource.StockTrader.controller;
import ch.qos.logback.classic.util.StatusViaSLF4JLoggerFactory;
import com.restResource.StockTrader.entity.CommandType;
import com.restResource.StockTrader.entity.logging.DebugEventLog;
import com.restResource.StockTrader.entity.logging.ErrorEventLog;
import com.restResource.StockTrader.entity.logging.UserCommandLog;
import com.restResource.StockTrader.repository.AccountRepository;
//import com.restResource.StockTrader.service.CustomConfigurationFactory;
import com.restResource.StockTrader.service.LoggingService;
//import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import org.slf4j.LoggerFactory;

@RestController
public class AddController {

    private AccountRepository accountRepository;
    private LoggingService loggingService;
    private Logger logger = LoggerFactory.getLogger(AddController.class);
    //private LoggerContext ctx = (LoggerContext) LogManager.getContext(false);

    public AddController(AccountRepository accountRepository,
                         LoggingService loggingService) {
        this.accountRepository = accountRepository;
        this.loggingService = loggingService;
    }

    @PutMapping(value = "/add")
    public @ResponseBody
    HttpStatus addToAccountBalance(@RequestParam String userId,
                                   @RequestParam int amount,
                                   @RequestParam int transactionNum) {

        Marker commandMarker = MarkerFactory.getMarker("userCommand");
        logger.debug(commandMarker,"arg1,arg2,arg3,arg4,arg5,arg6,arg7,arg8");
        System.out.println("hello");


        loggingService.logUserCommand(
                UserCommandLog.builder()
                        .command(CommandType.ADD)
                        .username(userId)
                        .transactionNum(transactionNum)
                        .funds(amount)
                        .build());
        try {
            if (amount <= 0) {
                throw new IllegalArgumentException(
                        "The ADD amount parameter must be greater than zero");
            } else {
                accountRepository.updateAccountBalance(userId, amount,transactionNum,"TS1");
            }
            return HttpStatus.OK;
        } catch (Exception e) {
            System.out.println("Exception in AddController: " + e.toString());
            loggingService.logErrorEvent(
                    ErrorEventLog.builder()
                            .command(CommandType.ADD)
                            .username(userId)
                            .transactionNum(transactionNum)
                            .funds(amount)
                            .errorMessage("Amount added must be less than or equal to zero")
                            .build());
            return HttpStatus.BAD_REQUEST;
        }
    }

//    public void configLogs() {
//
//        //        builder.setConfigurationName(name);
////        builder.setStatusLevel(Level.ERROR);
////        builder.add(builder.newFilter("ThresholdFilter", Filter.Result.ACCEPT, Filter.Result.NEUTRAL).
////                addAttribute("level", Level.DEBUG));
////        AppenderComponentBuilder appenderBuilder = builder.newAppender("Stdout", "CONSOLE").
////                addAttribute("target", ConsoleAppender.Target.SYSTEM_OUT);
////        appenderBuilder.add(builder.newLayout("PatternLayout").
////                addAttribute("pattern", "%d [%t] %-5level: %msg%n%throwable"));
////        appenderBuilder.add(builder.newFilter("MarkerFilter", Filter.Result.ACCEPT,
////                Filter.Result.NEUTRAL).addAttribute("marker", "FLOW"));
////        builder.add(appenderBuilder);
////        builder.add(builder.newLogger("org.apache.logging.log4j", Level.DEBUG).
////                add(builder.newAppenderRef("Stdout")).
////                addAttribute("additivity", false));
////        builder.add(builder.newRootLogger(Level.ERROR).add(builder.newAppenderRef("Stdout")));
////        return builder.build();
//
//
//        ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory.newConfigurationBuilder();
//        builder.setStatusLevel(Level.DEBUG);
//        builder.setConfigurationName("BuilderTest");
//        AppenderComponentBuilder appenderBuilder = builder.newAppender("Stdout", "CONSOLE")
//                .addAttribute("target",
//                ConsoleAppender.Target.SYSTEM_OUT);
//        appenderBuilder.add(builder.newLayout("PatternLayout")
//                .addAttribute("pattern", "%d [%t] %-5level: %msg%n%throwable"));
//        appenderBuilder.add(builder.newFilter("MarkerFilter", Filter.Result.ACCEPT, Filter.Result.NEUTRAL)
//                .addAttribute("marker", "FLOW"));
//        builder.add(appenderBuilder);
//        builder.add(builder.newLogger("org.apache.logging.log4j", Level.DEBUG)
//                .add(builder.newAppenderRef("Stdout")).addAttribute("additivity", false));
//        builder.add(builder.newRootLogger(Level.DEBUG).add(builder.newAppenderRef("Stdout")));
//        this.ctx = Configurator.initialize(builder.build());
//        ctx.updateLoggers();
//    }
}