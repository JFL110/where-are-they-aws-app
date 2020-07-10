package org.jfl110.mylocation.photos;

import java.io.InputStream;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.inject.Inject;

import org.jfl110.app.Logger;
import org.jfl110.mylocation.MyLocationAppConfig;
import org.jfl110.mylocation.S3Configutation;
import org.jfl110.mylocation.photos.ExtractGeoExifData.ExtractedPhotoDetails;
import org.jfl110.util.ExceptionUtils;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.iterable.S3Objects;
import com.google.common.collect.ImmutableList;

/**
 * Loop through all photos in the tenant S3 bucket and insert Photo records for
 * them. Delete any Photo records that didn't have a corresponding entry in the
 * bucket.
 * 
 * @author jim
 */
class ProcessS3PhotoBucket {

	private static final String PHOTO_DIR = "photos/";

	private final S3Configutation s3Config;
	private final Logger logger;
	private final PhotoDao photoDao;

	@Inject
	ProcessS3PhotoBucket(S3Configutation s3Config, Logger logger, PhotoDao photoDao) {
		this.s3Config = s3Config;
		this.logger = logger;
		this.photoDao = photoDao;
	}


	ExposedSyncS3PhotosOutput process(String tenantId) {
		ImmutableList.Builder<String> logLines = ImmutableList.builder();
		AmazonS3 s3client = AmazonS3ClientBuilder.standard()
				.withCredentials(s3Config.getCredentials())
				.withRegion(s3Config.getRegion())
				.build();

		// Load all photos in the bucket
		List<Photo> items = StreamSupport
				.stream(S3Objects.inBucket(s3client, s3Config.getBucketName()).spliterator(), false)
				// Objects within the photo directory
				.filter(objectSummary -> objectSummary.getKey().startsWith(PHOTO_DIR + tenantId + "/"))
				// Filter directories
				.filter(objectSummary -> !objectSummary.getKey().endsWith("/"))
				// Ignore webp copies
				.filter(objectSummary -> !objectSummary.getKey().endsWith(".webp"))
				.map(objectSummary -> {
					return ExceptionUtils.doRethrowing(() -> {
						try (InputStream is = s3client.getObject(s3Config.getBucketName(), objectSummary.getKey()).getObjectContent()) {

							logger.log("Processing object " + objectSummary.getKey());

							Optional<ExtractedPhotoDetails> details = new ExtractGeoExifData().extract(is, logger);
							if (!details.isPresent()) {
								String msg = "Warning : no geo data found for [" + objectSummary.getKey() + "]";
								logLines.add(msg);
								logger.log(msg);
								return null;
							}

							logLines.add("Got geodata for [" + objectSummary.getKey() + "]");

							String url = "https://" + s3Config.getBucketName() + MyLocationAppConfig.S3_DOMAIN + objectSummary.getKey();

							return new Photo(
									objectSummary.getKey(),
									details.get().getLatitude(),
									details.get().getLongitude(),
									url,
									Optional.empty(),
									ZonedDateTime.of(details.get().getTime(), ZoneId.of("UTC")),
									Optional.empty(),
									Optional.empty());
						}
					});
				}).filter(o -> o != null).collect(Collectors.toList());

		// Save all photos
		photoDao.save(tenantId, items);

		// Delete all photos that were not saved
		photoDao.listAll(tenantId)
				.filter(i -> !items.stream().anyMatch(o -> i.getId().equals(o.getId())))
				.forEach(i -> photoDao.delete(tenantId, i));

		// Return log lines as output
		return new ExposedSyncS3PhotosOutput(logLines.build());
	}
}
