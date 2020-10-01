package org.jfl110.mylocation;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.jfl110.aws.BadInputGatewayResponseException;
import org.jfl110.aws.GatewayEventInformation;
import org.jfl110.aws.GatewayRequestHandler;
import org.jfl110.aws.GatewayResponse;
import org.jfl110.aws.GatewayResponseBuilder;
import org.jfl110.mylocation.photos.GoogleDrivePhotoLocationDao;
import org.jfl110.mylocation.photos.PhotoDao;
import org.jfl110.util.LambdaUtils;
import org.jfl110.util.StringUtils;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Handler to digest all location data and photos and write to S3
 * 
 * @author jim
 *
 */
public class WriteAllPointsSummaryToS3Handler implements GatewayRequestHandler<ExposedSecurityKeyInput> {

	private static final float MIN_ACCURACY_METERS = 500;

	private final S3FileWriter s3FileWriter;
	private final LocationsDao logLocationDAO;
	private final GoogleDrivePhotoLocationDao googleDrivePhotoLocationDao;
	private final SecurityKeyProvider securityKeyProvider;
	private final PhotoDao photoDao;
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Inject
	WriteAllPointsSummaryToS3Handler(S3FileWriter s3FileWriter,
			LocationsDao logLocationDAO,
			GoogleDrivePhotoLocationDao googleDrivePhotoLocationDao,
			SecurityKeyProvider securityKeyProvider,
			PhotoDao photoDao) {
		this.s3FileWriter = s3FileWriter;
		this.logLocationDAO = logLocationDAO;
		this.googleDrivePhotoLocationDao = googleDrivePhotoLocationDao;
		this.securityKeyProvider = securityKeyProvider;
		this.photoDao = photoDao;
	}


	@Override
	public GatewayResponse handleRequest(ExposedSecurityKeyInput secretKeyInput, GatewayEventInformation eventInfo, Context context)
			throws IOException {
		if (secretKeyInput == null)
			throw new BadInputGatewayResponseException(BadInputGatewayResponseException.NO_INPUT_MESSAGE);
		if (StringUtils.isBlank(secretKeyInput.getTenantId()) ||
				StringUtils.isBlank(secretKeyInput.getSecurityKey()) ||
				!securityKeyProvider.getSecurityKey(secretKeyInput.getTenantId()).orElse("").equals(secretKeyInput.getSecurityKey())) {
			throw new BadInputGatewayResponseException(MyLocationAppConfig.BAD_SECURITY_KEY);
		}

		// Map all locations
		List<PointWithTime> locations = logLocationDAO.listAllAuto(secretKeyInput.getTenantId())
				.filter(l -> l.getAccuracy() < MIN_ACCURACY_METERS)
				.map(l -> new PointWithTime(new Point(l.getLatitude(), l.getLongitude()), l.getTime()))
				.collect(Collectors.toList());

		locations.addAll(
				LambdaUtils.mapList(logLocationDAO.listAllManual(secretKeyInput.getTenantId()),
						m -> new PointWithTime(new Point(m.getLatitude(), m.getLongitude()), m.getTime())));

		locations.addAll(
				LambdaUtils.mapList(googleDrivePhotoLocationDao.listAll(secretKeyInput.getTenantId()),
						m -> new PointWithTime(new Point(m.getLatitude(), m.getLongitude()), m.getTime())));

		// Photos
		List<Photo> photos = LambdaUtils.mapList(photoDao.listAll(secretKeyInput.getTenantId()),
				p -> new Photo(p.getUrl(), new Point(p.getLatitude(), p.getLongitude()), p.getTime().toInstant().toEpochMilli()));

		// Calculate most recent point
		Optional<PointWithTime> mostRecentPoint = locations.stream().sorted((p1, p2) -> p2.time.compareTo(p1.time)).findFirst();

		// Format output - remove duplicate points
		LocationSummary summary = new LocationSummary(
				LambdaUtils.mapSet(locations, p -> p.point),
				photos,
				mostRecentPoint.map(p -> p.point).orElse(null),
				mostRecentPoint.map(p -> p.time.toInstant().toEpochMilli()).orElse(null));

		// Write to S3
		s3FileWriter.writeJsonToPointsFile(secretKeyInput.getTenantId(), objectMapper.writeValueAsString(summary));

		return GatewayResponseBuilder.gatewayResponse().ok().stringBody(summary.points.size() + " points written").build();
	}

	/**
	 * JSON format for the output file
	 */
	public static class LocationSummary {

		private final Set<Point> points;
		private final List<Photo> photos;
		private final Point mostRecentPoint;
		private final Long mostRecentPointTime;

		LocationSummary(Set<Point> points, List<Photo> photos, Point mostRecentPoint, Long mostRecentPointTime) {
			this.points = points;
			this.photos = photos;
			this.mostRecentPoint = mostRecentPoint;
			this.mostRecentPointTime = mostRecentPointTime;
		}


		@JsonProperty("photos")
		public List<Photo> getPhotos() {
			return photos;
		}


		@JsonProperty("mostRecentPoint")
		public Point getMostRecentPoint() {
			return mostRecentPoint;
		}


		@JsonProperty("mostRecentPointTime")
		public Long getMostRecentPointTime() {
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

	public static class Photo {

		private final String url;
		private final Point point;
		private final long time;

		Photo(String url, Point point, long time) {
			this.url = url;
			this.point = point;
			this.time = time;
		}


		@JsonProperty("point")
		public Point getPoint() {
			return point;
		}


		@JsonProperty("time")
		public long getTime() {
			return time;
		}


		@JsonProperty("url")
		public String getUrl() {
			return url;
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
