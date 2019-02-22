package com.navishkaar.service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    @Value("${crawler.url}")
	private String crawlerUrl;
 
    @Async("asyncExecutor")
    public void getGoogleSearch( int taskId, String searchString,String url,int index) 
    {
    	String generatedUrl = crawlerUrl+"/"+taskId+"/"+searchString+"?index="+index+"&url="+url;
    	logger.info("getEmployeeName starts : "+generatedUrl);
    	
        restTemplate.getForObject(generatedUrl, Object.class);
 
       
    }
 
}
