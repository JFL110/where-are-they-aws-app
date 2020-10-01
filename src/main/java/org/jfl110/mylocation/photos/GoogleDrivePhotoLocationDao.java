package org.jfl110.mylocation.photos;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.jfl110.genericitem.GenericItem;
import org.jfl110.genericitem.GenericItemDao;
import org.jfl110.genericitem.GenericItemKey;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class GoogleDrivePhotoLocationDao {

	private final static DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_DATE_TIME;
	private static final GenericItemKey.Template KEY = GenericItemKey.template("DrivePhotoLoc").jsonVersion(1).tennantIdRequired();
	private static final String PARTITION_KEY = "DrivePhotoLoc";

	private final GenericItemDao dao;

	@Inject
	GoogleDrivePhotoLocationDao(GenericItemDao dao) {
		this.dao = dao;
	}


	/**
	 * Save a collection
	 */
	void save(String tenantId, GoogleDrivePhotoLocation i) {
		dao.put(GenericItem.genericItem(KEY.key()
				.withPartitionKey(PARTITION_KEY)
				.withSortKey(i.getId())
				.withTennantId(tenantId))
				.withContentJson(map(i))
				.build());
	}


	/**
	 * List all saved
	 */
	public Stream<GoogleDrivePhotoLocation> listAll(String tenantId) {
		return dao.queryByPartition(KEY.queryKey()
				.withTennantId(tenantId)
				.withPartitionKey(PARTITION_KEY)
				.build())
				.map(this::map);
	}


	private GoogleDrivePhotoLocationItem map(GoogleDrivePhotoLocation i) {
		return new GoogleDrivePhotoLocationItem(i.getLatitude(), i.getLongitude(), DATE_FORMAT.format(i.getTime()));
	}


	private GoogleDrivePhotoLocation map(GenericItem i) {
		GoogleDrivePhotoLocationItem c = i.contentAs(GoogleDrivePhotoLocationItem.class);
		return new GoogleDrivePhotoLocation(i.sortKey(), c.latitude, c.longitude, ZonedDateTime.parse(c.time, DATE_FORMAT));
	}

	/**
	 * Json format for storing manually logged locations
	 */
	public static class GoogleDrivePhotoLocationItem {

		@JsonProperty("lat") private final double latitude;
		@JsonProperty("long") private final double longitude;
		@JsonProperty("time") private final String time;

		@JsonCreator
		public GoogleDrivePhotoLocationItem(
				@JsonProperty("lat") double latitude,
				@JsonProperty("long") double longitude,
				@JsonProperty("time") String time) {
			this.latitude = latitude;
			this.longitude = longitude;
			this.time = time;
		}
	}
}
