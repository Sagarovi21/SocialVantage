package com.navishkaar.service;

import java.io.IOException;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CustomGoogleSearchEngine {
	@Value("${google.key}")
	private String accessKey;
	@Value("${google.cx}")
	private String googleCX;

	public String search(String searchTerm, int start) throws IOException {
		if (start > 100)
			return null;
		String[] spilletedSearchTerm = searchTerm.split(" ");
		StringBuilder sb = new StringBuilder("");
		for (String temp : spilletedSearchTerm)
			sb.append(temp);
		final String uri= String.format("https://www.googleapis.com/customsearch/v1?key=%s&cx=%s&q=%s&start=%d",accessKey.trim(), googleCX.trim(),sb.toString(),start);
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
		ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
		return result.getBody();

	}

}
