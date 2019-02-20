package com.example.demo.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "reviews")
public class Reviews {

	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "rec_num", nullable = false)
    private int rec_num;
	
	@Column(name = "task_id", nullable = false)
    private int taskId;
	
	@Column(name = "ObjectName", nullable = false, length = 150)
    private String objectName;
	
	@Column(name = "Review", length = 1000)
    private String review;
	
	@Column(name = "Rating")
    private float rating;
	
	@Column(name = "TotalRating")
    private float totalRating;
	
	@Column(name = "price")
    private double price;
	
	@Column(name = "product_details", length = 250)
    private String productDetails;
	
	@Column(name = "technial_details", length = 250)
    private String technialDetails;
	
	@Column(name = "feature_names", length = 250)
    private String featureNames;
	
	@Column(name = "feature_values", length = 250)
    private String featureValues;

	public int getTaskId() {
		return taskId;
	}

	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}

	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
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

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getProductDetails() {
		return productDetails;
	}

	public void setProductDetails(String productDetails) {
		this.productDetails = productDetails;
	}

	public String getTechnialDetails() {
		return technialDetails;
	}

	public void setTechnialDetails(String technialDetails) {
		this.technialDetails = technialDetails;
	}

	public String getFeatureNames() {
		return featureNames;
	}

	public void setFeatureNames(String featureNames) {
		this.featureNames = featureNames;
	}

	public String getFeatureValues() {
		return featureValues;
	}

	public void setFeatureValues(String featureValues) {
		this.featureValues = featureValues;
	}

	public Reviews(int taskId, String objectName, String review, float rating, float totalRating, double price,
			String featureNames, String featureValues) {
		super();
		this.taskId = taskId;
		this.objectName = objectName;
		this.review = review;
		this.rating = rating;
		this.totalRating = totalRating;
		this.price = price;
		this.featureNames = featureNames;
		this.featureValues = featureValues;
	}

	public Reviews() {
		super();
	}

	@Override
	public String toString() {
		return "Reviews [taskId=" + taskId + ", objectName=" + objectName + ", review=" + review + ", rating=" + rating
				+ ", totalRating=" + totalRating + ", price=" + price + ", productDetails=" + productDetails
				+ ", technialDetails=" + technialDetails + ", featureNames=" + featureNames + ", featureValues="
				+ featureValues + "]";
	}
	
	
	
}
