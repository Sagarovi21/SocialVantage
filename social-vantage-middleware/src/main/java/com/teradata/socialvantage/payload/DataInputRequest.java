package com.teradata.socialvantage.payload;

public class DataInputRequest implements Input{

	private String input;
	private String category;
	
	public DataInputRequest(String input, String category) {
		super();
		this.input = input;
		this.category = category;
	}
	
	public String getInput() {
		return input;
	}
	public void setInput(String input) {
		this.input = input;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	
	
}
