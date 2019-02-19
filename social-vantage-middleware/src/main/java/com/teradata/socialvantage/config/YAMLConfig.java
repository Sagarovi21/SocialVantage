package com.teradata.socialvantage.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties
public class YAMLConfig {

	private String name;
    private String environment;
    private String localservers;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEnvironment() {
		return environment;
	}
	public void setEnvironment(String environment) {
		this.environment = environment;
	}
	public String getLocalservers() {
		return localservers;
	}
	public void setLocalservers(String localservers) {
		this.localservers = localservers;
	}
    
    
}
