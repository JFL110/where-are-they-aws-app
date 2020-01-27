package org.jfl110.mylocation;

import java.time.format.DateTimeFormatter;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

/**
 * DynamoDBTable for manually logged locations.
 * 
 * @author jim
 *
 */
@DynamoDBTable(tableName = "MyLocation_ManualLocationItem")
public class ManualLocationItem {

	final static DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_ZONED_DATE_TIME;

	private String id;
	private String title; // Optional
	private String notes; // Optional
	private double latitude;
	private double longitude;
	private Float accuracy;
	private String time;
	private boolean night;
	private String endTime; // Optional

	@DynamoDBHashKey(attributeName = "id")
	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	@DynamoDBAttribute(attributeName = "latitude")
	public double getLatitude() {
		return latitude;
	}


	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}


	@DynamoDBAttribute(attributeName = "longitude")
	public double getLongitude() {
		return longitude;
	}


	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}


	@DynamoDBAttribute(attributeName = "time")
	public String getTime() {
		return time;
	}


	public void setTime(String time) {
		this.time = time;
	}


	@DynamoDBAttribute(attributeName = "endTime")
	public String getEndTime() {
		return endTime;
	}


	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	@DynamoDBAttribute(attributeName = "title")
	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}

	@DynamoDBAttribute(attributeName = "notes")
	public String getNotes() {
		return notes;
	}


	public void setNotes(String notes) {
		this.notes = notes;
	}

	@DynamoDBAttribute(attributeName = "accuracy")
	public Float getAccuracy() {
		return accuracy;
	}


	public void setAccuracy(Float accuracy) {
		this.accuracy = accuracy;
	}
	
	@DynamoDBAttribute(attributeName = "night")
	public boolean isNight() {
		return night;
	}
	
	
	public void setNight(boolean isNight) {
		night = isNight;
	}


	@Override
	public String toString() {
		return "ManualLocationItem[id=" + id + "]";
	}
}