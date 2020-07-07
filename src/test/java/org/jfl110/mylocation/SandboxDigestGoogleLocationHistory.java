package org.jfl110.mylocation;

import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * Utility to take location data exported from Google Takeout and insert into
 * LogLocationItem.
 * 
 * DO NOT COMMIT DynamoDB CREDENTIALS!
 * 
 * @author jim
 *
 */
public class SandboxDigestGoogleLocationHistory {

	// private static final double LATLONGFACTOR = 10000000;
	//
	// private final String jsonPath = "/home/jim/Downloads/2019_DECEMBER.json";
	// private final String activitySegmentIdPrefix = "2019JanActivity-";
	// private final float accuracy = 100;
	// private final String deviceName = "j-moto-gmaps";
	// private final ObjectMapper objectMapper = new ObjectMapper();

	@Test
	@Ignore
	public void digest() throws JsonProcessingException, IOException {

		// JsonNode rootNode = objectMapper.readTree(new File(jsonPath));
		// JsonNode timelineObjects = rootNode.get("timelineObjects");
		//
		// ImmutableList.Builder<LogLocationItem> items = ImmutableList.builder();
		// for (int i = 0; i < timelineObjects.size(); i++) {
		// JsonNode place = timelineObjects.get(i);
		//
		// try {
		// double latitude =
		// place.get("placeVisit").get("location").get("latitudeE7").asLong() /
		// LATLONGFACTOR;
		// double longitude =
		// place.get("placeVisit").get("location").get("longitudeE7").asLong() /
		// LATLONGFACTOR;
		// ZonedDateTime startTime =
		// toZonedDateTime(place.get("placeVisit").get("duration").get("startTimestampMs").asLong());
		// String name = place.get("placeVisit").get("location").has("name") ?
		// place.get("placeVisit").get("location").get("name").asText()
		// : null;
		// String id = place.get("placeVisit").get("location").get("placeId").asText();
		// System.out.println(latitude + "-" + longitude + "-" + startTime + "-" + name
		// + "-" + id);
		// LogLocationItem item = new LogLocationItem();
		// item.setId(activitySegmentIdPrefix + id);
		// item.setLatitude(latitude);
		// item.setLongitude(longitude);
		// item.setTime(LogLocationItem.DATE_FORMAT.format(startTime));
		// item.setAltitude(0);
		// item.setAccuracy(accuracy);
		// item.setDeviceName(deviceName);
		// item.setSavedTime(LogLocationItem.DATE_FORMAT.format(ZonedDateTime.now()));
		// items.add(item);
		// } catch (Exception e) {
		// }
		// try {
		// double startLatitude =
		// place.get("activitySegment").get("startLocation").get("latitudeE7").asLong()
		// / LATLONGFACTOR;
		// double startLongitude =
		// place.get("activitySegment").get("startLocation").get("longitudeE7").asLong()
		// / LATLONGFACTOR;
		// double endLatitude =
		// place.get("activitySegment").get("endLocation").get("latitudeE7").asLong() /
		// LATLONGFACTOR;
		// double endLongitude =
		// place.get("activitySegment").get("endLocation").get("longitudeE7").asLong() /
		// LATLONGFACTOR;
		// ZonedDateTime startTime =
		// toZonedDateTime(place.get("activitySegment").get("duration").get("startTimestampMs").asLong());
		// ZonedDateTime endTime =
		// toZonedDateTime(place.get("activitySegment").get("duration").get("endTimestampMs").asLong());
		// String startId = activitySegmentIdPrefix + "s-" + i;
		// String endId = activitySegmentIdPrefix + "e-" + i;
		// System.out.println(startLatitude + "-" + startLongitude + "-" + startTime +
		// "-" + startId + " : " + endLatitude + "-" + endLongitude
		// + "-" + endTime + "-" + endId);
		//
		// LogLocationItem itemStart = new LogLocationItem();
		// itemStart.setId(startId);
		// itemStart.setLatitude(startLatitude);
		// itemStart.setLongitude(startLongitude);
		// itemStart.setTime(LogLocationItem.DATE_FORMAT.format(startTime));
		// itemStart.setAltitude(0);
		// itemStart.setAccuracy(accuracy);
		// itemStart.setDeviceName(deviceName);
		// itemStart.setSavedTime(LogLocationItem.DATE_FORMAT.format(ZonedDateTime.now()));
		// items.add(itemStart);
		//
		// LogLocationItem itemEnd = new LogLocationItem();
		// itemEnd.setId(endId);
		// itemEnd.setLatitude(endLatitude);
		// itemEnd.setLongitude(endLongitude);
		// itemEnd.setTime(LogLocationItem.DATE_FORMAT.format(endTime));
		// itemEnd.setAltitude(0);
		// itemEnd.setAccuracy(accuracy);
		// itemEnd.setDeviceName(deviceName);
		// itemEnd.setSavedTime(LogLocationItem.DATE_FORMAT.format(ZonedDateTime.now()));
		// items.add(itemEnd);
		// } catch (Exception e) {
		// // Ignore
		// }
		// }
		//
		// // Local
		// AmazonDynamoDB db = AmazonDynamoDBClientBuilder.standard()
		// .withEndpointConfiguration(new
		// EndpointConfiguration("http://192.168.43.209:8000", "")).build();
		// // Remote
		// // AmazonDynamoDB db
		// //
		// =AmazonDynamoDBClientBuilder.standard().withRegion(Regions.EU_WEST_2).withCredentials(new
		// // AWSStaticCredentialsProvider(new BasicAWSCredentials("", ""))).build();
		// DynamoDBMapper mapper = new DynamoDBMapper(db);
		// items.build().forEach(i -> mapper.save(i));
		// FluentIterable.from(mapper.scan(LogLocationItem.class, new
		// DynamoDBScanExpression())).forEach(System.out::println);
	}

	// private ZonedDateTime toZonedDateTime(long utcEpochMillis) {
	// return ZonedDateTime.ofInstant(Instant.ofEpochMilli(utcEpochMillis),
	// ZoneId.of("UTC"));
	// }
}
