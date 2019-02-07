package com.teradata.socialvantage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackages={"com.teradata.socialvantage"},
		exclude = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration.class}
        )
public class SocialVantageServerApplication{

	public static void main(String[] args) {
		SpringApplication.run(SocialVantageServerApplication.class, args);
	}

}

