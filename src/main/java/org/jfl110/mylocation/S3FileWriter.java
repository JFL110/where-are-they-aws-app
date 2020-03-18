package org.jfl110.mylocation;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

import javax.inject.Inject;

import org.jfl110.util.StringUtils;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

public class S3FileWriter {

	private static final String S3_JSON_BUCKET_NAME = "S3_JSON_BUCKET_NAME";
	private static final String S3_JSON_FILE_NAME = "S3_JSON_FILE_NAME";

	private final S3CredentialsSupplier s3CredentialsSupplier;

	@Inject
	S3FileWriter(S3CredentialsSupplier s3CredentialsSupplier) {
		this.s3CredentialsSupplier = s3CredentialsSupplier;
	}


	void writeJsonToPointsFile(String json) {
		String bucketName = System.getenv(S3_JSON_BUCKET_NAME);
		String jsonFileName = System.getenv(S3_JSON_FILE_NAME);
		if (StringUtils.isBlank(bucketName)) {
			throw new IllegalStateException("S3_JSON_BUCKET_NAME not specified");
		}
		if (StringUtils.isBlank(jsonFileName)) {
			throw new IllegalStateException("S3_JSON_FILE_NAME not specified");
		}
		
		AmazonS3Client s3client = new AmazonS3Client(s3CredentialsSupplier.get());
		
		ObjectMetadata metadata = new ObjectMetadata();
	    metadata.setContentType("application/json");
	    PutObjectRequest request = new PutObjectRequest(bucketName, jsonFileName, new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)), metadata);
	    request.setCannedAcl(CannedAccessControlList.PublicRead);
		s3client.putObject(request);
	}

	static class S3CredentialsSupplier implements Supplier<AWSStaticCredentialsProvider> {

		private static final String S3_ACCESS_KEY = "S3_ACCESS_KEY";
		private static final String S3_SECRET_KEY = "S3_SECRET_KEY";

		@Override
		public AWSStaticCredentialsProvider get() {
			String accessKey = System.getenv(S3_ACCESS_KEY);
			String secretKey = System.getenv(S3_SECRET_KEY);
			if (StringUtils.isBlank(accessKey)) {
				throw new IllegalStateException("S3 access key not specified");
			}
			if (StringUtils.isBlank(secretKey)) {
				throw new IllegalStateException("S3 access key not specified");
			}

			return new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey));
		}
	}
}
