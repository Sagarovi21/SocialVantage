package com.navishkaar.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.mashape.unirest.http.exceptions.UnirestException;
import com.navishkaar.core.CustomResult;
import com.navishkaar.core.EbayResponse;
import com.navishkaar.service.CustomGoogleSearchService;
import com.navishkaar.service.EBaySearchService;
import com.navishkaar.service.FacebookSearchService;
import com.navishkaar.service.TwitterSearchService;

import facebook4j.FacebookException;
import facebook4j.Post;
import facebook4j.ResponseList;
import twitter4j.TwitterException;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class SearchController {

	private static Logger logger = LoggerFactory.getLogger(CustomGoogleSearchService.class);
	
	@Autowired
	private CustomGoogleSearchService customGoogleSearchService;
	@Autowired
	private TwitterSearchService twitterSearchService;
	@Autowired
	private FacebookSearchService facebookSearchService;
	@Autowired
	private EBaySearchService eBaySearchService;

	@GetMapping(path = "/google/{searchTerm}", produces = "application/json")
	public String searchGoogle(@PathVariable("searchTerm") String searchTerm) throws IOException {
		logger.info("Searching for "+searchTerm);
		return customGoogleSearchService.search(searchTerm).toString();
	}

	@GetMapping(path = "/google/v1/{searchTerm}", produces = "application/json")
	public List<CustomResult> searchGoogleCustom(@PathVariable("searchTerm") String searchTerm) throws Exception {
		logger.info("Searching for "+searchTerm);
		return customGoogleSearchService.searchGoogleCustom(searchTerm);
	}

	@GetMapping(path = "/twitter/v1/{searchTerm}", produces = "application/json")
	public List<Status> searchTwitter(@PathVariable("searchTerm") String searchTerm) throws TwitterException {
		logger.info("Searching for "+searchTerm);
		return twitterSearchService.search(searchTerm);
	}

	@GetMapping(path = "/facebook/v1/{searchTerm}", produces = "application/json")
	public ResponseList<Post> searchFacebook(@PathVariable("searchTerm") String searchTerm)
			throws TwitterException, FacebookException {
		return facebookSearchService.search(searchTerm);
	}

	@GetMapping(path = "/ebay/v1/{searchTerm}", produces = "application/json")
	public List<EbayResponse> searchEBay(@PathVariable("searchTerm") String searchTerm)
			throws TwitterException, FacebookException, UnsupportedEncodingException, UnirestException {
		return eBaySearchService.search(searchTerm);
	}
}
