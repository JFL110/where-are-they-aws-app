package org.jfl110.mylocation.photos;

import java.time.ZonedDateTime;
import java.util.Optional;

public class Photo {

	private final String id;
	private final double latitude;
	private final double longitude;
	private final String url;
	private final Optional<String> thumbnailUrl;
	private final ZonedDateTime time;
	private final Optional<String> title;
	private final Optional<String> description;

	public Photo(String id,
			double latitude,
			double longitude,
			String url,
			Optional<String> thumbnailUrl,
			ZonedDateTime time,
			Optional<String> title,
			Optional<String> description) {
		this.id = id;
		this.latitude = latitude;
		this.longitude = longitude;
		this.url = url;
		this.thumbnailUrl = thumbnailUrl;
		this.time = time;
		this.title = title;
		this.description = description;
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


	public String getUrl() {
		return url;
	}


	public Optional<String> getThumbnailUrl() {
		return thumbnailUrl;
	}


	public ZonedDateTime getTime() {
		return time;
	}


	public Optional<String> getTitle() {
		return title;
	}


	public Optional<String> getDescription() {
		return description;
	}

}