package org.jfl110.mylocation;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

/**
 * JSON Bean output for {@link ShowAllLocationsJsonHandler}
 * 
 * OUTPUT ONLY
 * 
 * @author jim
 *
 */
public class ExposedShowLogLocationsOutput {

	private final ImmutableList<Location> locations;

	@JsonCreator
	public ExposedShowLogLocationsOutput(@JsonProperty("locations") List<Location> locations) {
		this.locations = ImmutableList.copyOf(locations);
	}


	@JsonProperty("locations")
	public ImmutableList<Location> getLocations() {
		return locations;
	}

	public static class Location {

		private final String id;
		private final double latitude;
		private final double longitude;
		private final Double altitude;
		private final float accuracy;
		private final long recordedTimeUtcMillis;
		private final String title;
		private final String pointType;


		@JsonCreator
		public Location(@JsonProperty("id") String id, @JsonProperty("la") double latitude, @JsonProperty("lo") double longitude,
				@JsonProperty("al") Double altitude, @JsonProperty("ac") float accuracy, @JsonProperty("t") long recordedTimeUtcMillis, 
				@JsonProperty("ti")  String title, @JsonProperty("ty")  String pointType) {
			this.id = id;
			this.latitude = latitude;
			this.longitude = longitude;
			this.altitude = altitude;
			this.accuracy = accuracy;
			this.recordedTimeUtcMillis = recordedTimeUtcMillis;
			this.title = title;
			this.pointType = pointType;
		}


		@JsonProperty("id")
		public String getId() {
			return id;
		}


		@JsonProperty("la")
		public double getLatitude() {
			return latitude;
		}


		@JsonProperty("lo")
		public double getLongitude() {
			return longitude;
		}


		@JsonProperty("t")
		public long getRecordedTimeMillis() {
			return recordedTimeUtcMillis;
		}


		@JsonProperty("ac")
		public float getAccuracy() {
			return accuracy;
		}


		@JsonProperty("al")
		public Double getAltitude() {
			return altitude;
		}

		
		@JsonProperty("ti")
		public String getTitle() {
			return title;
		}
		
		@JsonProperty("ty")
		public String getPointType() {
			return pointType;
		}


		@Override
		public String toString() {
			return "[ID:" + id + ",lat:" + latitude + ",long:" + longitude + ",time:" + recordedTimeUtcMillis + "]";
		}
	}
}