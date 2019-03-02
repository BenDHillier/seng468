package com.restResource.StockTrader;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import redis.clients.jedis.Jedis;

import java.util.concurrent.Executor;

@SpringBootApplication
@EnableAsync
public class StockTraderApplication {

	public static void main(String[] args) {
		SpringApplication.run(StockTraderApplication.class, args);
	}

	//technically we dont need this but i decided to add it anyway.
	@Bean
	public Executor taskExecutor(){
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.initialize();
		return executor;
	}

	@Bean
	public Jedis jedisObject() {
		//TODO this will need to be changed since it wont be localhost when its stored in a docker container
		Jedis jedis = new Jedis("localhost", 6379);
		return jedis;
	}


}

