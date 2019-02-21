package com.teradata.socialvantage.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "TaskStatus")
public class TaskStatus {

	@Id @GeneratedValue(strategy=GenerationType.IDENTITY, generator="native")
	@Column(name = "rec_num", nullable = false)
    private int rec_num;
	
	@Column(name = "task_id", nullable = false)
    private int taskId;
	
	@Column(name = "task", nullable = false, length = 20)
    private String task;
	
	@Column(name = "search", length = 150)
    private String search;
	
	@Column(name = "category", length = 150)
    private String category;
	
	@Column(name = "pages_found")
    private int pagesFound;
	
	@Column(name = "pages_completed")
    private int pagesCompleted;
	
	@Column(name = "comments_found")
    private int commentsFound;
	
	@Column(name = "task_status",length = 7)
    private String taskStatus;

	public int getRec_num() {
		return rec_num;
	}

	public void setRec_num(int rec_num) {
		this.rec_num = rec_num;
	}

	public int getTaskId() {
		return taskId;
	}

	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}

	public String getTask() {
		return task;
	}

	public void setTask(String task) {
		this.task = task;
	}

	public String getSearch() {
		return search;
	}

	public void setSearch(String search) {
		this.search = search;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public int getPagesFound() {
		return pagesFound;
	}

	public void setPagesFound(int pagesFound) {
		this.pagesFound = pagesFound;
	}

	public int getPagesCompleted() {
		return pagesCompleted;
	}

	public void setPagesCompleted(int pagesCompleted) {
		this.pagesCompleted = pagesCompleted;
	}

	public int getCommentsFound() {
		return commentsFound;
	}

	public void setCommentsFound(int commentsFound) {
		this.commentsFound = commentsFound;
	}

	public String getTaskStatus() {
		return taskStatus;
	}

	public void setTaskStatus(String taskStatus) {
		this.taskStatus = taskStatus;
	}

	public TaskStatus() {
		super();
	}

	public TaskStatus(int taskId, String task, String taskStatus) {
		super();
		this.taskId = taskId;
		this.task = task;
		this.taskStatus = taskStatus;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((task == null) ? 0 : task.hashCode());
		result = prime * result + taskId;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TaskStatus other = (TaskStatus) obj;
		if (task == null) {
			if (other.task != null)
				return false;
		} else if (!task.equals(other.task))
			return false;
		if (taskId != other.taskId)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TaskStatus [taskId=" + taskId + ", task=" + task + ", search=" + search + ", category=" + category
				+ ", pagesFound=" + pagesFound + ", pagesCompleted=" + pagesCompleted + ", commentsFound="
				+ commentsFound + ", taskStatus=" + taskStatus + "]";
	}
	
	
	
}
