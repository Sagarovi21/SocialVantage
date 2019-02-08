package com.teradata.socialvantage.payload;

public class Result {

	private String entity;
	private Double score;
	private Integer rank;
	
	public Result(String entity, Double score, Integer rank) {
		super();
		this.entity = entity;
		this.score = score;
		this.rank = rank;
	}
	public String getEntity() {
		return entity;
	}
	public void setEntity(String entity) {
		this.entity = entity;
	}
	public Double getScore() {
		return score;
	}
	public void setScore(Double score) {
		this.score = score;
	}
	public Integer getRank() {
		return rank;
	}
	public void setRank(Integer rank) {
		this.rank = rank;
	}
	
	
}
