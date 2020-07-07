package org.jfl110.mylocation;

import java.time.ZonedDateTime;
import java.util.Optional;

public class ManualLogLocation {

	private final String id;
	private final Optional<String> title;
	private final Optional<String> notes;
	private final double latitude;
	private final double longitude;
	private final Optional<Float> accuracy;
	private final ZonedDateTime time;
	private final boolean night;
	private final Optional<ZonedDateTime> endTime;

	ManualLogLocation(String id,
			Optional<String> title,
			Optional<String> notes,
			double latitude,
			double longitude,
			Optional<Float> accuracy,
			ZonedDateTime time,
			boolean night,
			Optional<ZonedDateTime> endTime) {
		this.id = id;
		this.title = title;
		this.notes = notes;
		this.latitude = latitude;
		this.longitude = longitude;
		this.accuracy = accuracy;
		this.time = time;
		this.night = night;
		this.endTime = endTime;
	}


	public String getId() {
		return id;
	}


	public Optional<String> getTitle() {
		return title;
	}


	public Optional<String> getNotes() {
		return notes;
	}


	public double getLatitude() {
		return latitude;
	}


	public double getLongitude() {
		return longitude;
	}


	public Optional<Float> getAccuracy() {
		return accuracy;
	}


	public ZonedDateTime getTime() {
		return time;
	}


	public boolean isNight() {
		return night;
	}


	public Optional<ZonedDateTime> getEndTime() {
		return endTime;
	}

}
