package com.teradata.socialvantage.payload;

public class ResponseOutput {

	private int taskId;
	private String status;
	private DataInputRequest input;
	public int getTaskId() {
		return taskId;
	}
	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public DataInputRequest getInput() {
		return input;
	}
	public void setInput(DataInputRequest input) {
		this.input = input;
	}
	public ResponseOutput(int taskId, String status, DataInputRequest input) {
		super();
		this.taskId = taskId;
		this.status = status;
		this.input = input;
	}
	public ResponseOutput() {
		super();
	}
	
	
}
