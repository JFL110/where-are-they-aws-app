package org.jfl110.mylocation.photos;

import java.time.ZonedDateTime;

import javax.inject.Inject;

import org.jfl110.aws.dynamodb.DynamoDBMapperFront;
import org.jfl110.aws.dynamodb.ZonedDateTimeDynamoDBMapper;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverted;
import com.google.common.collect.FluentIterable;

public class PhotoDao {

	private final DynamoDBMapperFront db;

	@Inject
	PhotoDao(DynamoDBMapperFront db) {
		this.db = db;
	}
	
	void delete(PhotoItem item) {
		db.delete(item);
	}


	void save(PhotoItem item) {
		db.save(item);
	}


	public FluentIterable<PhotoItem> listAll() {
		return db.listAll(PhotoItem.class);
	}

	@DynamoDBTable(tableName = "MyLocation_Photo")
	public static class PhotoItem {

		private String id;
		private double latitude;
		private double longitude;
		private String url;
		private String thumbnailUrl;
		private ZonedDateTime time;
		private String title;
		private String description;

		PhotoItem(String id, double latitude, double longitude, String url, String thumbnailUrl, ZonedDateTime time, String title,
				String description) {
			this.id = id;
			this.latitude = latitude;
			this.longitude = longitude;
			this.url = url;
			this.thumbnailUrl = thumbnailUrl;
			this.time = time;
			this.title = title;
			this.description = description;
		}


		public PhotoItem() {
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


		@DynamoDBAttribute(attributeName = "url")
		public String getUrl() {
			return url;
		}


		public void setUrl(String url) {
			this.url = url;
		}


		@DynamoDBAttribute(attributeName = "thumbnailUrl")
		public String getThumbnailUrl() {
			return thumbnailUrl;
		}


		public void setThumbnailUrl(String thumbnailUrl) {
			this.thumbnailUrl = thumbnailUrl;
		}


		@DynamoDBAttribute(attributeName = "time")
		@DynamoDBTypeConverted(converter = ZonedDateTimeDynamoDBMapper.class)
		public ZonedDateTime getTime() {
			return time;
		}


		@DynamoDBAttribute(attributeName = "time")
		public void setTime(ZonedDateTime time) {
			this.time = time;
		}


		@DynamoDBAttribute(attributeName = "title")
		public String getTitle() {
			return title;
		}


		public void setTitle(String title) {
			this.title = title;
		}


		@DynamoDBAttribute(attributeName = "description")
		public String getDescription() {
			return description;
		}


		public void setDescription(String description) {
			this.description = description;
		}
	}
}
