package org.jfl110.mylocation;

import org.jfl110.util.StringUtils;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;

public class S3Configutation {

	private static final String S3_ACCESS_KEY = "S3_ACCESS_KEY";
	private static final String S3_SECRET_KEY = "S3_SECRET_KEY";
	private static final String S3_JSON_BUCKET_NAME = "S3_JSON_BUCKET_NAME";
	private static final String S3_JSON_FILE_NAME = "S3_JSON_FILE_NAME";

	public AWSStaticCredentialsProvider getCredentials() {
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


	public String getBucketName() {
		String bucketName = System.getenv(S3_JSON_BUCKET_NAME);
		if (StringUtils.isBlank(bucketName)) {
			throw new IllegalStateException("S3_JSON_BUCKET_NAME not specified");
		}
		return bucketName;
	}


	String getPointsFileName() {
		String jsonFileName = System.getenv(S3_JSON_FILE_NAME);
		if (StringUtils.isBlank(jsonFileName)) {
			throw new IllegalStateException("S3_JSON_FILE_NAME not specified");
		}
		return jsonFileName;
	}
}