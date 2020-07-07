package org.jfl110.mylocation;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.jfl110.genericitem.GenericItem;
import org.jfl110.genericitem.GenericItemDao;
import org.jfl110.genericitem.GenericItemKey;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DAO for Manual and Auto logged location items
 * 
 * @author jim
 *
 */
class LocationsDao {

	private static final String FIXED_PARTITION_KEY = "loc";
	static final String DEFAULT_ITEM_SORT_KEY = "def";

	private final static DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_DATE_TIME;
	private static final GenericItemKey.Template AUTO_KEY = GenericItemKey.template("AutoLoc").jsonVersion(1).tennantIdRequired();
	private static final GenericItemKey.Template MANUAL_KEY = GenericItemKey.template("ManLoc").jsonVersion(1).tennantIdRequired();

	private final GenericItemDao dao;

	@Inject
	LocationsDao(GenericItemDao dao) {
		this.dao = dao;
	}


	/**
	 * Save a collection of AutoLogLocation
	 */
	void save(String tenantId, List<AutoLogLocation> items) {
		dao.batchWriteManager().doInWriteBatch(
				() -> items.forEach(i -> dao.put(GenericItem.genericItem(AUTO_KEY.key()
						.withPartitionKey(FIXED_PARTITION_KEY)
						.withSortKey(i.getId())
						.withTennantId(tenantId))
						.withContentJson(map(i))
						.build())));
	}


	/**
	 * List all saved AutoLogLocation
	 */
	Stream<AutoLogLocation> listAllAuto(String tenantId) {
		return dao.queryByPartition(AUTO_KEY.queryKey()
				.withTennantId(tenantId)
				.withPartitionKey(FIXED_PARTITION_KEY)
				.build()).map(this::mapAuto);
	}


	/**
	 * List all saved ManualLogLocation
	 */
	Stream<ManualLogLocation> listAllManual(String tenantId) {
		return dao.queryByPartition(MANUAL_KEY.queryKey()
				.withTennantId(tenantId)
				.withPartitionKey(FIXED_PARTITION_KEY)
				.build())
				.filter(g -> !DEFAULT_ITEM_SORT_KEY.equals(g.sortKey()))
				.map(this::mapManual);
	}


	/**
	 * Save the default ManualLogLocation item
	 */
	void saveDefaultManualItem(String tenantId, ManualLogLocation i) {
		if (!DEFAULT_ITEM_SORT_KEY.equals(i.getId())) {
			throw new IllegalArgumentException("ID must be default");
		}
		saveManualItem(tenantId, i);
	}


	void saveManualItem(String tenantId, ManualLogLocation i) {
		dao.put(GenericItem.genericItem(MANUAL_KEY.key()
				.withPartitionKey(FIXED_PARTITION_KEY)
				.withSortKey(i.getId())
				.withTennantId(tenantId))
				.withContentJson(map(i))
				.build());
	}


	/**
	 * Check if the default ManualLogLocation item exists
	 */
	boolean defaultItemExists(String tenantId) {
		return dao.exists((MANUAL_KEY.key()
				.withPartitionKey(FIXED_PARTITION_KEY)
				.withSortKey(DEFAULT_ITEM_SORT_KEY)
				.withTennantId(tenantId).build()));
	}


	private ManualLocationItem map(ManualLogLocation i) {
		return new ManualLocationItem(i.getTitle(),
				i.getNotes(),
				i.getLatitude(),
				i.getLongitude(),
				i.getAccuracy(),
				DATE_FORMAT.format(i.getTime()),
				i.isNight(),
				i.getEndTime().map(DATE_FORMAT::format));
	}


	private ManualLogLocation mapManual(GenericItem g) {
		ManualLocationItem i = g.contentAs(ManualLocationItem.class);
		return new ManualLogLocation(
				g.sortKey(),
				i.title,
				i.notes,
				i.latitude,
				i.longitude,
				i.accuracy,
				ZonedDateTime.parse(i.time, DATE_FORMAT),
				i.night,
				i.endTime.map(t -> ZonedDateTime.parse(t, DATE_FORMAT)));
	}


	private AutoLocationItem map(AutoLogLocation i) {
		return new AutoLocationItem(i.getLatitude(),
				i.getLongitude(),
				i.getAltitude(),
				i.getAccuracy(),
				i.getDeviceName(),
				i.getTime(),
				i.getSavedTime(),
				i.getEndTime());
	}


	private AutoLogLocation mapAuto(GenericItem g) {
		AutoLocationItem i = g.contentAs(AutoLocationItem.class);
		return new AutoLogLocation(g.sortKey(),
				i.latitude,
				i.longitude,
				i.altitude,
				i.accuracy,
				i.deviceName,
				i.time,
				i.savedTime,
				i.endTime);
	}

	public static class AutoLocationItem {

		@JsonProperty("lat") private final double latitude;
		@JsonProperty("lon") private final double longitude;
		@JsonProperty("alt") private final double altitude;
		@JsonProperty("acc") private final float accuracy;
		@JsonProperty("dev") private final String deviceName;
		@JsonProperty("tim") private final ZonedDateTime time;
		@JsonProperty("sTim") private final ZonedDateTime savedTime;
		@JsonProperty("eTim") private final Optional<ZonedDateTime> endTime;

		@JsonCreator
		public AutoLocationItem(@JsonProperty("lat") double latitude,
				@JsonProperty("lon") double longitude,
				@JsonProperty("alt") double altitude,
				@JsonProperty("acc") float accuracy,
				@JsonProperty("dev") String deviceName,
				@JsonProperty("tim") ZonedDateTime time,
				@JsonProperty("sTim") ZonedDateTime savedTime,
				@JsonProperty("eTim") Optional<ZonedDateTime> endTime) {
			this.latitude = latitude;
			this.longitude = longitude;
			this.altitude = altitude;
			this.accuracy = accuracy;
			this.deviceName = deviceName;
			this.time = time;
			this.savedTime = savedTime;
			this.endTime = endTime;
		}
	}

	/**
	 * Json format for storing manually logged locations
	 */
	public static class ManualLocationItem {

		@JsonProperty("title") private final Optional<String> title;
		@JsonProperty("notes") private final Optional<String> notes;
		@JsonProperty("lat") private final double latitude;
		@JsonProperty("long") private final double longitude;
		@JsonProperty("acc") private final Optional<Float> accuracy;
		@JsonProperty("time") private final String time;
		@JsonProperty("night") private final boolean night;
		@JsonProperty("endTime") private final Optional<String> endTime;

		@JsonCreator
		public ManualLocationItem(
				@JsonProperty("title") Optional<String> title,
				@JsonProperty("notes") Optional<String> notes,
				@JsonProperty("lat") double latitude,
				@JsonProperty("long") double longitude,
				@JsonProperty("acc") Optional<Float> accuracy,
				@JsonProperty("time") String time,
				@JsonProperty("night") boolean night,
				@JsonProperty("endTime") Optional<String> endTime) {
			this.title = title;
			this.notes = notes;
			this.latitude = latitude;
			this.longitude = longitude;
			this.accuracy = accuracy;
			this.time = time;
			this.night = night;
			this.endTime = endTime;
		}
	}
}
