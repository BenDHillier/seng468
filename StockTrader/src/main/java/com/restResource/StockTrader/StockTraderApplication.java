package com.restResource.StockTrader;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.sql.DataSource;
import java.util.concurrent.Executor;

@SpringBootApplication
@EnableAsync
public class StockTraderApplication {

	public static void main(String[] args) { SpringApplication.run(StockTraderApplication.class, args); }

	//technically we dont need this but i decided to add it anyway.
	@Bean
	public Executor taskExecutor(){
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(2); //Dont think we need this
		executor.setMaxPoolSize(2);
		executor.setQueueCapacity(500); //not sure what number this should be, might be too small -> dunno if there is a way to make it dynamic
		executor.initialize();
		return executor;
	}
}

