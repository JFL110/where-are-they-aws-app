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
import org.jfl110.mylocation.S3Configutation;
import org.jfl110.mylocation.photos.ExtractGeoExifData.ExtractedPhotoDetails;
import org.jfl110.util.ExceptionUtils;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.iterable.S3Objects;
import com.google.common.collect.ImmutableList;

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


	ExposedSyncS3PhotosOutput process() {
		ImmutableList.Builder<String> logLines = ImmutableList.builder();
		AmazonS3Client s3client = new AmazonS3Client(s3Config.getCredentials());
		List<PhotoDao.PhotoItem> items = StreamSupport.stream(S3Objects.inBucket(s3client, s3Config.getBucketName()).spliterator(), false)
				.filter(objectSummary -> objectSummary.getKey().startsWith(PHOTO_DIR)).map(objectSummary -> {
					// ObjectMetadata meta = s3client.getObjectMetadata(s3Config.getBucketName(),
					// objectSummary.getKey());
					return ExceptionUtils.doRethrowing(() -> {
						try (InputStream is = s3client.getObject(s3Config.getBucketName(), objectSummary.getKey()).getObjectContent()) {
							Optional<ExtractedPhotoDetails> details = new ExtractGeoExifData().extract(is);
							if (!details.isPresent()) {
								String msg = "Warning : no geo data found for [" + objectSummary.getKey() + "]";
								logLines.add(msg);
								logger.log(msg);
								return null;
							}
							
							logLines.add("Got geodata for [" + objectSummary.getKey() + "]");
							
							String url = "https://" + s3Config.getBucketName() + ".s3.eu-west-2.amazonaws.com/" + objectSummary.getKey();
							return new PhotoDao.PhotoItem(objectSummary.getKey(), details.get().getLatitude(), details.get().getLongitude(), url,
									null, ZonedDateTime.of(details.get().getTime(), ZoneId.of("UTC")), null, null);
						}
					});
				}).filter(o -> o != null).collect(Collectors.toList());
		
		items.forEach(photoDao::save);
		photoDao.listAll().filter(i -> !items.stream().anyMatch(o -> i.getId().equals(o.getId()))).forEach(photoDao::delete);
		return new ExposedSyncS3PhotosOutput(logLines.build());
	}
}
