package com.navishkaar.controller;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.navishkaar.service.GoogleSearchService;

@RestController
public class SearchController {

	@Autowired
	private GoogleSearchService googleSearchService;
	@GetMapping("/google/{searchTerm}")
	public Map<String, String> searchGoogle(@PathVariable("searchTerm") String searchTerm) throws IOException
	{
		return googleSearchService.search(searchTerm);
	}
}