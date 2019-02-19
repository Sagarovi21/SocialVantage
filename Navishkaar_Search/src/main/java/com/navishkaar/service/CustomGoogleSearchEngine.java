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

@Service
public class CustomGoogleSearchEngine {

	public String search(String searchTerm, int start) throws IOException {
		if (start > 100)
			return null;
		String[] spilletedSearchTerm = searchTerm.split(" ");
		StringBuilder sb = new StringBuilder("");
		for (String temp : spilletedSearchTerm)
			sb.append(temp);
		final String uri = "https://www.googleapis.com/customsearch/v1?key=AIzaSyDrn3sLmBU8SIGSe67LROTt-81hq6sbJ6o&cx=016303953285139242562:6-wrexhcdku&q="
				+ sb.toString() + "&start=" + start;
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
		ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
		return result.getBody();

	}

}
