package com.navishkaar.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.mashape.unirest.http.exceptions.UnirestException;
import com.navishkaar.core.CustomResult;
import com.navishkaar.core.EbayResponse;
import com.navishkaar.entity.TaskStatus;
import com.navishkaar.repository.TaskStatusRepository;
import com.navishkaar.service.AsyncService;
import com.navishkaar.service.CustomGoogleSearchService;
import com.navishkaar.service.EBaySearchService;
import com.navishkaar.service.FacebookSearchService;
import com.navishkaar.service.TwitterSearchService;
import com.navishkaar.worker.Worker;

import facebook4j.FacebookException;
import facebook4j.Post;
import facebook4j.ResponseList;
import twitter4j.TwitterException;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class SearchController {

	private static Logger logger = LoggerFactory.getLogger(CustomGoogleSearchService.class);
	
	@Autowired
	private ObjectFactory<Worker> workerFactory;
	
	Worker getWorker() {
		return workerFactory.getObject();
	}

	@Autowired
	private AsyncService service;
	@Autowired
	private TaskStatusRepository taskStatusRepository;
	@Autowired
	private CustomGoogleSearchService customGoogleSearchService;
	@Autowired
	private TwitterSearchService twitterSearchService;
	@Autowired
	private FacebookSearchService facebookSearchService;
	@Autowired
	private EBaySearchService eBaySearchService;

	@GetMapping(path = "/google/{taskId}/{searchTerm}", produces = "application/json")
	public void searchGoogle(@PathVariable("taskId") int taskId , @PathVariable("searchTerm") String searchTerm) throws IOException {
		logger.info("Searching for "+searchTerm);
		Map<String, String> searchResults = customGoogleSearchService.search(searchTerm);
		int index = 1;
		for(Map.Entry<String, String> entry: searchResults.entrySet()) {
			service.getGoogleSearch(taskId,searchTerm,entry.getValue(),index);
			index++;
		}
		int size = searchResults.size();
		updateTaskStatus(taskId, searchTerm, size);
	}

	private void updateTaskStatus(int taskId, String searchTerm, int size) {
		String task ="Google_Crawl";
		String status = "Submit";
		TaskStatus taskStatus = new TaskStatus(taskId, task, status);
		taskStatus.setSearch(searchTerm);
		taskStatus.setIndex(-1);
		taskStatus.setPagesFound(size);
		taskStatusRepository.save(taskStatus);
	}

	@GetMapping(path = "/google/v1/{taskId}/{searchTerm}", produces = "application/json")
	public void searchGoogleCustom(@PathVariable("taskId") int taskId ,@PathVariable("searchTerm") String searchTerm) throws Exception {
		logger.info("Searching for "+searchTerm);
		List<CustomResult> results = customGoogleSearchService.searchGoogleCustom(searchTerm);
		int index = 1;
		for(CustomResult customResult: results) {
			service.getGoogleSearch(taskId,searchTerm,customResult.getUrl(),index);
			index++;
		}
		int size = results.size();
		updateTaskStatus(taskId, searchTerm, size);
		 
	}

	/*
	 * @GetMapping(path = "/twitter/v1/{searchTerm}", produces = "application/json")
	 * public List<Status> searchTwitter(@PathVariable("searchTerm") String
	 * searchTerm) throws TwitterException {
	 * logger.info("Searching for "+searchTerm); return
	 * twitterSearchService.search(searchTerm); }
	 */
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
