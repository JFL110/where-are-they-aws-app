package org.jfl110.mylocation;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.jfl110.dynamodb.AmazonDynamoDBSupplier;
import org.jfl110.dynamodb.DynamoDBMapperFront;
import org.jfl110.dynamodb.DynamoDBTablePrefixModule;
import org.jfl110.genericitem.DynamoGenericItemModule;
import org.junit.Ignore;
import org.junit.Test;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.google.common.collect.ImmutableList;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Run migration of log locations from old table structure
 * 
 * @author jim
 *
 */
@Ignore
public class MigrateToGenericItem {

	@Test
	public void migrate() {

		AmazonDynamoDB db = AmazonDynamoDBClientBuilder.standard().withRegion(Regions.EU_WEST_2)
				.withCredentials(
						new AWSStaticCredentialsProvider(
								new BasicAWSCredentials(
										"",
										"")))
				.build();

		Injector injector = Guice.createInjector(
				new DynamoDBTablePrefixModule(MyLocationAppConfig.TABLE_NAME_PREFIX),
				b -> b.bind(AmazonDynamoDBSupplier.class).toInstance(() -> db),
				new DynamoGenericItemModule());

		LocationsDao dao = injector.getInstance(LocationsDao.class);
		DynamoDBMapperFront front = injector.getInstance(DynamoDBMapperFront.class);

		// Manual Locations
		front.listAll(ManualLocationItem.class)
				.filter(m -> !m.id.equals("template"))
				.transform(this::map)
				.toList()
				.stream()
				.peek(i -> System.out.println(i.getId()))
				.forEach(m -> dao.saveManualItem("live", m));

		// Auto Locations
		front.listAll(LogLocationItem.class)
				.transform(this::map)
				.toList()
				.stream()
				.peek(i -> System.out.println(i.getId()))
				.forEach(m -> dao.save("live", ImmutableList.of(m)));
	}


	private ManualLogLocation map(ManualLocationItem i) {
		return new ManualLogLocation(i.id,
				Optional.ofNullable(i.title),
				Optional.ofNullable(i.notes),
				i.latitude,
				i.longitude,
				Optional.ofNullable(i.accuracy),
				ZonedDateTime.parse(i.time, ManualLocationItem.DATE_FORMAT),
				i.isNight(),
				Optional.ofNullable(i.endTime).map(t -> ZonedDateTime.parse(t, ManualLocationItem.DATE_FORMAT)));
	}


	private AutoLogLocation map(LogLocationItem i) {
		return new AutoLogLocation(i.id,
				i.latitude,
				i.longitude,
				i.altitude,
				i.accuracy,
				i.deviceName,
				ZonedDateTime.parse(i.time, ManualLocationItem.DATE_FORMAT),
				ZonedDateTime.parse(i.savedTime, ManualLocationItem.DATE_FORMAT),
				Optional.ofNullable(i.endTime).map(t -> ZonedDateTime.parse(t, ManualLocationItem.DATE_FORMAT)));
	}

	@DynamoDBTable(tableName = "ManualLocationItem")
	public static class ManualLocationItem {

		final static DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_ZONED_DATE_TIME;

		private String id;
		private String title; // Optional
		private String notes; // Optional
		private double latitude;
		private double longitude;
		private Float accuracy;
		private String time;
		private boolean night;
		private String endTime; // Optional

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


		@DynamoDBAttribute(attributeName = "title")
		public String getTitle() {
			return title;
		}


		public void setTitle(String title) {
			this.title = title;
		}


		@DynamoDBAttribute(attributeName = "notes")
		public String getNotes() {
			return notes;
		}


		public void setNotes(String notes) {
			this.notes = notes;
		}


		@DynamoDBAttribute(attributeName = "accuracy")
		public Float getAccuracy() {
			return accuracy;
		}


		public void setAccuracy(Float accuracy) {
			this.accuracy = accuracy;
		}


		@DynamoDBAttribute(attributeName = "night")
		public boolean isNight() {
			return night;
		}


		public void setNight(boolean isNight) {
			night = isNight;
		}


		@Override
		public String toString() {
			return "ManualLocationItem[id=" + id + "]";
		}
	}

	@DynamoDBTable(tableName = "LogLocation")
	public static class LogLocationItem {

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

		LogLocationItem(String id,
				double latitude,
				double longitude,
				double altitude,
				float accuracy,
				String deviceName,
				String time,
				String savedTime,
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


		public LogLocationItem() {
		}


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
}
