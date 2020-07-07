package org.jfl110.mylocation;

import java.time.ZonedDateTime;
import java.util.Optional;

public class AutoLogLocation {

	private final String id;
	private final double latitude;
	private final double longitude;
	private final double altitude;
	private final float accuracy;
	private final String deviceName;
	private final ZonedDateTime time;
	private final ZonedDateTime savedTime;
	private final Optional<ZonedDateTime> endTime;

	AutoLogLocation(String id,
			double latitude,
			double longitude,
			double altitude,
			float accuracy,
			String deviceName,
			ZonedDateTime time,
			ZonedDateTime savedTime,
			Optional<ZonedDateTime> endTime) {
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


	public String getId() {
		return id;
	}


	public double getLatitude() {
		return latitude;
	}


	public double getLongitude() {
		return longitude;
	}


	public double getAltitude() {
		return altitude;
	}


	public float getAccuracy() {
		return accuracy;
	}


	public String getDeviceName() {
		return deviceName;
	}


	public ZonedDateTime getTime() {
		return time;
	}


	public ZonedDateTime getSavedTime() {
		return savedTime;
	}


	public Optional<ZonedDateTime> getEndTime() {
		return endTime;
	}
}
