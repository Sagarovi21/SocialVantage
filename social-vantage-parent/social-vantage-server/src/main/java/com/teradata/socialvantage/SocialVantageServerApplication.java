/*
 * Copyright (C) 2019  by Teradata Corporation.
 * All Rights Reserved.
 * TERADATA CONFIDENTIAL AND TRADE SECRET
 * 
 * @author Meeraj Kanaparthi
 * 
 */

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

