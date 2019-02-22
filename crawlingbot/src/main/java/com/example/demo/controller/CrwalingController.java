package com.example.demo.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.AsyncService;
import com.example.demo.worker.Worker;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api")	
public class CrwalingController {

	private static Logger logger = LoggerFactory.getLogger(CrwalingController.class);
	
	@Autowired
	private AsyncService service;
	@Autowired
	private ObjectFactory<Worker> workerFactory;
	
	Worker getWorker() {
		return workerFactory.getObject();
	}
	
	@GetMapping(path = "/search/{taskId}/{searchTerm}", produces = "application/json")
	@ResponseBody
	public String searchGoogleCustom(@PathVariable("taskId") int taskId ,
			@RequestParam("url") String url, @RequestParam("index") int index,
			@PathVariable("searchTerm") String searchTerm) throws Exception {
		logger.info("Searching for "+searchTerm);
		getWorker().dowork(url, taskId, searchTerm,  index);
		return "Done";
	}
	 
}
