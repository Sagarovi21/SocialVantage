package com.teradata.socialvantage.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.teradata.socialvantage.entity.TaskStatus;
import com.teradata.socialvantage.repository.TaskStatusRepository;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class TaskStatusController {

	private static Logger logger = LoggerFactory.getLogger(TaskStatusController.class);
	
	@Autowired
	private TaskStatusRepository taskStatusRepository;
	
	@RequestMapping(value = "/status",produces = {"application/JSON"} , consumes = {"application/JSON"})
	@ResponseBody
	public String saveStatus(@RequestBody TaskStatus taskStatus) {
		logger.info("Saving Status "+taskStatus);
		taskStatusRepository.save(taskStatus);
		return "done";
	}
	
	@RequestMapping(value = "/tasks",method = RequestMethod.GET)
	@ResponseBody
	public List<TaskStatus> getTasks() {
		logger.info("getting all  Status ");
		List<TaskStatus> tasks = new ArrayList<>();
		taskStatusRepository.findAll().forEach(tasks::add);
		logger.info("Size of all status "+tasks.size());
		return tasks;
	}
	
	@RequestMapping(value = "/task/{taskId}",method = RequestMethod.GET)
	@ResponseBody
	public List<TaskStatus> getTasks(@PathVariable("taskId") int taskId) {
		logger.info("getting all  Status for task Id : "+taskId);
		List<TaskStatus> tasks = new ArrayList<>();
		return taskStatusRepository.findByTaskId(taskId);
	}
}
