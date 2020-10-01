package org.jfl110.mylocation.photos;

import java.time.ZonedDateTime;

public class GoogleDrivePhotoLocation {

	private final String id;
	private final double latitude;
	private final double longitude;
	private final ZonedDateTime time;

	GoogleDrivePhotoLocation(String id, double latitude, double longitude, ZonedDateTime time) {
		this.id = id;
		this.latitude = latitude;
		this.longitude = longitude;
		this.time = time;
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


	public ZonedDateTime getTime() {
		return time;
	}

}