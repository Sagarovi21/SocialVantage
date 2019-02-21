package com.teradata.socialvantage.controller;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.teradata.socialvantage.entity.TaskStatus;
import com.teradata.socialvantage.payload.DataInputRequest;
import com.teradata.socialvantage.payload.Output;
import com.teradata.socialvantage.payload.ResponseOutput;
import com.teradata.socialvantage.repository.TaskStatusRepository;
import com.teradata.socialvantage.service.AsyncService;
import com.teradata.socialvantage.strategy.Processor;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class SocialVantageController {
	private static Logger logger = LoggerFactory.getLogger(SocialVantageController.class);
	
	@Autowired
	BeanFactory bf;
	
	@Autowired
	private TaskStatusRepository taskStatusRepository;
	
	@Autowired
	private AsyncService service;

	@RequestMapping(value = "/asyncSearch",produces = {"application/JSON"} , consumes = {"application/JSON"})
	@ResponseBody
	public ResponseOutput asyncSearch(@RequestBody DataInputRequest dataInputRequest) throws InterruptedException, ExecutionException {
		logger.info("Initiated the call : "+ dataInputRequest);
		CompletableFuture<Map<String,String>> googleLink = service.getGoogleSearch(dataInputRequest.getInput());
		int taskId=(int)(Math.random()*100000);
		String task ="Overall";
		String status = "Submit";
		ResponseOutput responseOutput = new ResponseOutput(taskId, status, dataInputRequest);
		TaskStatus taskStatus = new TaskStatus(taskId, task, status);
		taskStatus.setSearch(dataInputRequest.getInput());
		taskStatus.setCategory(dataInputRequest.getCategory());
		taskStatusRepository.save(taskStatus);
		logger.info("Completed the call");
		return responseOutput;
	}
	
	@RequestMapping(value = "/search" ,
			produces = {"application/JSON"} , consumes = {"application/JSON"})
	@ResponseBody
	public Output search(@RequestBody DataInputRequest dataInputRequest ) {
		Processor processor = bf.getBean(dataInputRequest.getCategory(),Processor.class);
		return processor.process(dataInputRequest);
	}
	

}
