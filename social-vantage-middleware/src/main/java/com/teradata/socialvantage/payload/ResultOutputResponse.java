package com.teradata.socialvantage.payload;

import java.util.List;

public class ResultOutputResponse implements Output{

	private DataInputRequest input ;
	private List<Result> output;
	
	public ResultOutputResponse(DataInputRequest input, List<Result> output) {
		super();
		this.input = input;
		this.output = output;
	}
	
	public DataInputRequest getInput() {
		return input;
	}
	public void setInput(DataInputRequest input) {
		this.input = input;
	}
	public List<Result> getOutput() {
		return output;
	}
	public void setOutput(List<Result> output) {
		this.output = output;
	}
	
	
	
}
