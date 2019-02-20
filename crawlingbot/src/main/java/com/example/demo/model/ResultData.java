package com.example.demo.model;

import java.util.List;

public class ResultData {

	private String review;
	private String feature;
	private String title;
	private Double price;
	private float rating;
	private float totalRating;

	public float getRating() {
		return rating;
	}
	public void setRating(float rating) {
		this.rating = rating;
	}
	public float getTotalRating() {
		return totalRating;
	}
	public void setTotalRating(float totalRating) {
		this.totalRating = totalRating;
	}
	public String getReview() {
		return review;
	}
	public void setReview(String review) {
		this.review = review;
	}
	public String getFeature() {
		return feature;
	}
	public void setFeature(String feature) {
		this.feature = feature;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	public ResultData(String review, String feature, String title, Double price) {
		super();
		this.review = review;
		this.feature = feature;
		this.title = title;
		this.price = price;
	}
	public ResultData(String review, String feature, String title, Double price, float rating, float totalRating) {
		super();
		this.review = review;
		this.feature = feature;
		this.title = title;
		this.price = price;
		this.rating = rating;
		this.totalRating = totalRating;
	}
	
	
	
	
}
