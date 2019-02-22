package com.navishkaar.model;

public class Review {

	private String review;
	private float rating;
	private float totalRating;
	
	public Review(String review, float rating, float totalRating) {
		super();
		this.review = review;
		this.rating = rating;
		this.totalRating = totalRating;
	}
	public String getReview() {
		return review;
	}
	public void setReview(String review) {
		this.review = review;
	}
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
	
	
}
