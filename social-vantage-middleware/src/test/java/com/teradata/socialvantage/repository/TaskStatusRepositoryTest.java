package com.teradata.socialvantage.repository;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.teradata.socialvantage.controller.TaskStatusController;
import com.teradata.socialvantage.entity.TaskStatus;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TaskStatusRepositoryTest {
	private static Logger logger = LoggerFactory.getLogger(TaskStatusController.class);
	
	@Autowired
	private TaskStatusRepository taskStatusRepository;
	
	@Test
	public void test() {
		taskStatusRepository.findByTaskId(37).forEach((task) -> logger.info(task.toString()));
		
	}

}
