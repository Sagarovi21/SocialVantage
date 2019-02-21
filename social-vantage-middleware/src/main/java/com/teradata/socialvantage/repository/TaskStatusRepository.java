package com.teradata.socialvantage.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.teradata.socialvantage.entity.TaskStatus;

public interface TaskStatusRepository extends CrudRepository<TaskStatus,Integer>{

	List<TaskStatus> findByTaskId(int taskId);
	
	@Query("SELECT t FROM TaskStatus t where t.id <> 'task'")
	List<TaskStatus> findAllTasks();
}
