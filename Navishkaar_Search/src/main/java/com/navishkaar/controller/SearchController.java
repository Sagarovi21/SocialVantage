package com.navishkaar.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

@RestController
public class SearchController {

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
		return customGoogleSearchService.search(searchTerm).toString();
	}

	@GetMapping(path = "/google/v1/{searchTerm}", produces = "application/json")
	public List<CustomResult> searchGoogleCustom(@PathVariable("searchTerm") String searchTerm) throws Exception {
		return customGoogleSearchService.searchGoogleCustom(searchTerm);
	}

	@GetMapping(path = "/twitter/v1/{searchTerm}", produces = "application/json")
	public List<String> searchTwitter(@PathVariable("searchTerm") String searchTerm) throws TwitterException {
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
