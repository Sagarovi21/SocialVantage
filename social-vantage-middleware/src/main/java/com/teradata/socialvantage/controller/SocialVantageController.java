package com.teradata.socialvantage.controller;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.teradata.socialvantage.payload.DataInputRequest;
import com.teradata.socialvantage.payload.Output;
import com.teradata.socialvantage.strategy.Processor;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class SocialVantageController {
	
	@Autowired
	BeanFactory bf;
	
	@RequestMapping(value = "/search" ,
			produces = {"application/JSON"} , consumes = {"application/JSON"})
	@ResponseBody
	public Output search(@RequestBody DataInputRequest dataInputRequest ) {
		Processor processor = bf.getBean(dataInputRequest.getCategory(),Processor.class);
		return processor.process(dataInputRequest);
	}
	

}
