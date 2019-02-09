package com.navishkaar.service;

import java.io.IOException;
import java.util.Arrays;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class GoogleCustomSearchService {
	
	public String search(String text)  {
		final String uri = "https://www.googleapis.com/customsearch/v1?key=AIzaSyDrn3sLmBU8SIGSe67LROTt-81hq6sbJ6o&cx=016303953285139242562:6-wrexhcdku&q="+text;
	     
	    RestTemplate restTemplate = new RestTemplate();
	     
	    HttpHeaders headers = new HttpHeaders();
	    headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
	    HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
	     
	    ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
		/*
		 * ObjectMapper mapper = new ObjectMapper(); return
		 * mapper.readTree(result.getBody());
		 */
	    return result.getBody();
	 
	}
	

}
