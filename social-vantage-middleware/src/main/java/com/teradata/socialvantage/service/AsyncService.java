package com.teradata.socialvantage.service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class AsyncService {

	private static Logger logger = LoggerFactory.getLogger(AsyncService.class);
	
	@Autowired
    private RestTemplate restTemplate;
 
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
 
    @Async("asyncExecutor")
    public CompletableFuture<String> getGoogleSearch(String searchString) 
    {
    	logger.info("getEmployeeName starts");
 
       String mapUrls = restTemplate.getForObject("http://localhost/search/google/"+searchString, String.class);
 
        logger.info("employeeNameData, {}", mapUrls);
       
        return CompletableFuture.completedFuture(mapUrls);
    }
 
}
