package com.example.demo;

import java.util.concurrent.Executor;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import com.example.demo.worker.CustomAsyncExceptionHandle;


@SpringBootApplication
@EnableAsync()
public class CrawlingbotApplication {

	public static void main(String[] args) {
		SpringApplication.run(CrawlingbotApplication.class, args);
	}

	@Bean(name = "threadPoolTaskExecutor")
	public Executor threadPoolTaskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(14);
		executor.setMaxPoolSize(14);
		executor.setThreadNamePrefix("Runner");
		executor.initialize();
		return executor;
	}


}
