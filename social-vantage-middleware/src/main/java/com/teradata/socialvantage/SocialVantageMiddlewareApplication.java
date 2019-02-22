package com.teradata.socialvantage;

import java.util.concurrent.Executor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.teradata.socialvantage.config.YAMLConfig;

@EnableAsync
@SpringBootApplication
public class SocialVantageMiddlewareApplication {

	@Autowired
    private YAMLConfig myConfig;
	
	public static void main(String[] args) {
		SpringApplication.run(SocialVantageMiddlewareApplication.class, args);
	}
	
	
	
	@Bean(name = "asyncExecutor")
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(3);
        executor.setMaxPoolSize(3);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("AsynchThread-");
        executor.initialize();
        return executor;
    }
	
	public void run(String... args) throws Exception {
        System.out.println("using environment: " + myConfig.getEnvironment());
        System.out.println("name: " + myConfig.getName());
        System.out.println("servers: " + myConfig.getLocalservers());
    }

}

