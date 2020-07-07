package org.jfl110.mylocation.photos;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.jfl110.genericitem.GenericItem;
import org.jfl110.genericitem.GenericItemDao;
import org.jfl110.genericitem.GenericItemKey;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PhotoDao {

	private static final String FIXED_PARTITION_KEY = "photo";
	private static final GenericItemKey.Template KEY = GenericItemKey.template("Photo").jsonVersion(1).tennantIdRequired();
	private final GenericItemDao dao;

	@Inject
	PhotoDao(GenericItemDao dao) {
		this.dao = dao;
	}


	void delete(String tenantId, Photo item) {
		dao.delete(KEY.key()
				.withPartitionKey(FIXED_PARTITION_KEY)
				.withTennantId(tenantId)
				.withSortKey(item.getId())
				.build());
	}


	void save(String tenantId, List<Photo> items) {
		dao.batchWriteManager().doInWriteBatch(() -> {
			items.forEach(
					item -> dao.put(GenericItem.genericItem(KEY.key()
							.withPartitionKey(FIXED_PARTITION_KEY)
							.withTennantId(tenantId)
							.withSortKey(item.getId()))
							.withContentJson(map(item))));
		});
	}


	public Stream<Photo> listAll(String tenantId) {
		return dao.queryByPartition(KEY.queryKey()
				.withPartitionKey(FIXED_PARTITION_KEY)
				.withTennantId(tenantId))
				.map(this::map);
	}


	private PhotoItem map(Photo p) {
		return new PhotoItem(p.getLatitude(),
				p.getLongitude(),
				p.getUrl(),
				p.getThumbnailUrl(),
				p.getTime(),
				p.getTitle(),
				p.getDescription());
	}


	private Photo map(GenericItem g) {
		PhotoItem i = g.contentAs(PhotoItem.class);
		return new Photo(
				g.sortKey(),
				i.latitude,
				i.longitude,
				i.url,
				i.thumbnailUrl,
				i.time,
				i.title,
				i.description);
	}

	/**
	 * Json format for storing photos
	 */
	public static class PhotoItem {

		@JsonProperty("lat") private final double latitude;
		@JsonProperty("lon") private final double longitude;
		@JsonProperty("url") private final String url;
		@JsonProperty("thumbUrl") private final Optional<String> thumbnailUrl;
		@JsonProperty("time") private final ZonedDateTime time;
		@JsonProperty("tile") private final Optional<String> title;
		@JsonProperty("desc") private final Optional<String> description;

		@JsonCreator
		public PhotoItem(@JsonProperty("lat") double latitude,
				@JsonProperty("lon") double longitude,
				@JsonProperty("url") String url,
				@JsonProperty("thumbUrl") Optional<String> thumbnailUrl,
				@JsonProperty("time") ZonedDateTime time,
				@JsonProperty("tile") Optional<String> title,
				@JsonProperty("desc") Optional<String> description) {
			this.latitude = latitude;
			this.longitude = longitude;
			this.url = url;
			this.thumbnailUrl = thumbnailUrl;
			this.time = time;
			this.title = title;
			this.description = description;
		}
	}
}
