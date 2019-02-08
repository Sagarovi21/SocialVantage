package com.teradata.socialvantage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.teradata.socialvantage.config.YAMLConfig;

@SpringBootApplication
public class SocialVantageMiddlewareApplication {

	@Autowired
    private YAMLConfig myConfig;
	
	public static void main(String[] args) {
		SpringApplication.run(SocialVantageMiddlewareApplication.class, args);
	}
	
	public void run(String... args) throws Exception {
        System.out.println("using environment: " + myConfig.getEnvironment());
        System.out.println("name: " + myConfig.getName());
        System.out.println("servers: " + myConfig.getLocalservers());
    }

}

