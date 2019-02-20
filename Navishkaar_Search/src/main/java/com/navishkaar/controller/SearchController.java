package com.navishkaar.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.navishkaar.core.CustomResult;
import com.navishkaar.service.CustomGoogleSearchService;
import com.navishkaar.service.FacebookSearchService;
import com.navishkaar.service.TwitterSearchService;

import facebook4j.FacebookException;
import facebook4j.Post;
import facebook4j.ResponseList;
import twitter4j.Status;
import twitter4j.TwitterException;

@RestController
public class SearchController {

	@Autowired
	private CustomGoogleSearchService customGoogleSearchService;
	@Autowired
	private TwitterSearchService twitterSearchService;
	@Autowired
	private FacebookSearchService facebookSearchService;

	@GetMapping(path = "/google/{searchTerm}", produces = "application/json")
	public String searchGoogle(@PathVariable("searchTerm") String searchTerm) throws IOException {
		return customGoogleSearchService.search(searchTerm).toString();
	}

	@GetMapping(path = "/google/v1/{searchTerm}", produces = "application/json")
	public List<CustomResult> searchGoogleCustom(@PathVariable("searchTerm") String searchTerm) throws Exception {
		return customGoogleSearchService.searchGoogleCustom(searchTerm);
	}

	@GetMapping(path = "/twitter/v1/{searchTerm}", produces = "application/json")
	public List<Status> searchTwitter(@PathVariable("searchTerm") String searchTerm) throws TwitterException {
		return twitterSearchService.search(searchTerm);
	}

	@GetMapping(path = "/facebook/v1/{searchTerm}", produces = "application/json")
	public ResponseList<Post> searchFacebook(@PathVariable("searchTerm") String searchTerm)
			throws TwitterException, FacebookException {
		return facebookSearchService.search(searchTerm);
	}
}
