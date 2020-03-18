package org.jfl110.mylocation;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.jfl110.app.Logger;
import org.jfl110.aws.GatewayEventInformation;
import org.jfl110.aws.GatewayRequestHandler;
import org.jfl110.aws.GatewayResponse;
import org.jfl110.aws.GatewayResponseBuilder;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

public class WriteAllPointsSummaryToS3Handler implements GatewayRequestHandler<String> {

	private static final float MIN_ACCURACY_METERS = 500;

	private final S3FileWriter s3FileWriter;
	private final LogLocationDAO logLocationDAO;
	private final ManualLocationsDAO manualLocationsDAO;
	private final Logger logger;
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Inject
	WriteAllPointsSummaryToS3Handler(S3FileWriter s3FileWriter, LogLocationDAO logLocationDAO, ManualLocationsDAO manualLocationsDAO, Logger logger) {
		this.s3FileWriter = s3FileWriter;
		this.logLocationDAO = logLocationDAO;
		this.manualLocationsDAO = manualLocationsDAO;
		this.logger = logger;
	}


	@Override
	public GatewayResponse handleRequest(String secretKeyInput, GatewayEventInformation eventInfo, Context context) throws IOException {
		// Map all locations
		List<PointWithTime> locations = logLocationDAO.listAll().filter(l -> l.getAccuracy() < MIN_ACCURACY_METERS).map(
				l -> new PointWithTime(new Point(l.getLatitude(), l.getLongitude()), ZonedDateTime.parse(l.getTime(), LogLocationItem.DATE_FORMAT)))
				.collect(Collectors.toList());
		locations.addAll(manualLocationsDAO.listAll().map(
				m -> new PointWithTime(new Point(m.getLatitude(), m.getLongitude()), ZonedDateTime.parse(m.getTime(), LogLocationItem.DATE_FORMAT)))
				.collect(Collectors.toList()));

		Optional<PointWithTime> mostRecentPoint = locations.stream().sorted((p1, p2) -> p1.time.compareTo(p2.time)).findFirst();

		LocationSummary summary = new LocationSummary(locations.stream().map(p -> p.point).collect(Collectors.toSet()),
				mostRecentPoint.map(p -> p.point).orElse(null), mostRecentPoint.map(p -> p.time.toString()).orElse(null));

		s3FileWriter.writeJsonToPointsFile(objectMapper.writeValueAsString(summary));

		return GatewayResponseBuilder.gatewayResponse().ok().stringBody(summary.points.size() + " points written").build();
	}


	@Override
	public Class<String> inputClazz() {
		return String.class;
	}

	public static class LocationSummary {

		private final Set<Point> points;
		private final Point mostRecentPoint;
		private final String mostRecentPointTime;

		LocationSummary(Set<Point> points, Point mostRecentPoint, String mostRecentPointTime) {
			this.points = points;
			this.mostRecentPoint = mostRecentPoint;
			this.mostRecentPointTime = mostRecentPointTime;
		}


		@JsonProperty("mostRecentPoint")
		public Point getMostRecentPoint() {
			return mostRecentPoint;
		}


		@JsonProperty("mostRecentPointTime")
		public String getMostRecentPointTime() {
			return mostRecentPointTime;
		}


		@JsonProperty("points")
		public Set<Point> getPoints() {
			return points;
		}

	}

	public static class PointWithTime {

		private final Point point;
		private final ZonedDateTime time;

		PointWithTime(Point point, ZonedDateTime time) {
			this.point = point;
			this.time = time;
		}
	}

	public static class Point {

		private final double latitude;
		private final double longitude;

		Point(double latitude, double longitude) {
			this.latitude = latitude;
			this.longitude = longitude;
		}


		@JsonProperty("l")
		public double getLatitude() {
			return latitude;
		}


		@JsonProperty("g")
		public double getLongitude() {
			return longitude;
		}


		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			long temp;
			temp = Double.doubleToLongBits(latitude);
			result = prime * result + (int) (temp ^ (temp >>> 32));
			temp = Double.doubleToLongBits(longitude);
			result = prime * result + (int) (temp ^ (temp >>> 32));
			return result;
		}


		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Point other = (Point) obj;
			if (Double.doubleToLongBits(latitude) != Double.doubleToLongBits(other.latitude))
				return false;
			if (Double.doubleToLongBits(longitude) != Double.doubleToLongBits(other.longitude))
				return false;
			return true;
		}
	}
}
