package org.jfl110.mylocation;


import java.time.format.DateTimeFormatter;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

/**
 * DynamoDBTable for logged locations.
 * 
 * @author jim
 *
 */
@DynamoDBTable(tableName = "MyLocation_LogLocation")
public class LogLocationItem {

	final static DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_ZONED_DATE_TIME;

	private String id;
	private double latitude;
	private double longitude;
	private double altitude;
	private float accuracy;
	private String deviceName;
	private String time;
	private String savedTime;
	private String endTime; // Optional
	
	LogLocationItem(String id, double latitude, double longitude, double altitude, float accuracy, String deviceName, String time, String savedTime,
			String endTime) {
		this.id = id;
		this.latitude = latitude;
		this.longitude = longitude;
		this.altitude = altitude;
		this.accuracy = accuracy;
		this.deviceName = deviceName;
		this.time = time;
		this.savedTime = savedTime;
		this.endTime = endTime;
	}
	
	public LogLocationItem() {}


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


	@DynamoDBAttribute(attributeName = "savedTime")
	public String getSavedTime() {
		return savedTime;
	}


	public void setSavedTime(String savedTime) {
		this.savedTime = savedTime;
	}

	@DynamoDBAttribute(attributeName = "altitude")
	public double getAltitude() {
		return altitude;
	}


	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}

	@DynamoDBAttribute(attributeName = "accuracy")
	public float getAccuracy() {
		return accuracy;
	}


	public void setAccuracy(float accuracy) {
		this.accuracy = accuracy;
	}

	@DynamoDBAttribute(attributeName = "deviceName")
	public String getDeviceName() {
		return deviceName;
	}


	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}


	@Override
	public String toString() {
		return "LogLocationItem[id=" + id + "]";
	}
}