package org.jfl110.mylocation;

import java.time.format.DateTimeFormatter;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

/**
 * JSON Bean input for {@link LogLocationsHandler}
 * 
 * INPUT ONLY
 * 
 * @author jim
 *
 */
public class ExposedLogLocationsInput {

	final static DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_DATE_TIME;

	private final String securityKey;
	private final String deviceName;
	private final ImmutableList<Location> locations;

	@JsonCreator
	public ExposedLogLocationsInput(@JsonProperty("locations") List<Location> locations, @JsonProperty("key") String securityKey, @JsonProperty("device") String deviceName) {
		this.locations = ImmutableList.copyOf(locations);
		this.securityKey = securityKey;
		this.deviceName = deviceName;
	}
	
	String getSecurityKey() {
		return securityKey;
	}
	

	ImmutableList<Location> getLocations() {
		return locations;
	}
	
	String getDeviceName() {
		return deviceName;
	}


	@Override
	public String toString() {
		return "Locations:[" + locations.toString() + "]";
	}

	public static class Location {

		private final String id;
		private final double latitude;
		private final double longitude;
		private final double altitude;
		private final float accuracy;
		private final String recordedTimeAsString;

		@JsonCreator
		public Location(@JsonProperty("id") String id, @JsonProperty("la") double latitude, @JsonProperty("lo") double longitude,
				@JsonProperty("al") double altitude, @JsonProperty("ac") float accuracy, 
				@JsonProperty("t") String recordedTimeAsString) {
			this.id = id;
			this.latitude = latitude;
			this.longitude = longitude;
			this.altitude = altitude;
			this.accuracy = accuracy;
			this.recordedTimeAsString = recordedTimeAsString;
		}


		String getId() {
			return id;
		}


		double getLatitude() {
			return latitude;
		}


		double getLongitude() {
			return longitude;
		}


		String getRecordedTimeAsString() {
			return recordedTimeAsString;
		}
		
		float getAccuracy() {
			return accuracy;
		}
		
		double getAltitude() {
			return altitude;
		}


		@Override
		public String toString() {
			return "[ID:" + id + ",lat:" + latitude + ",long:" + longitude + ",time:" + recordedTimeAsString + "]";
		}
	}
}