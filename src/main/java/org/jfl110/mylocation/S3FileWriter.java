package org.jfl110.mylocation;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import javax.inject.Inject;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

class S3FileWriter {

	private final S3Configutation s3Configutation;

	@Inject
	S3FileWriter(S3Configutation s3Configutation) {
		this.s3Configutation = s3Configutation;
	}


	void writeJsonToPointsFile(String tennatId, String json) {

		AmazonS3 s3client = AmazonS3ClientBuilder.standard()
				.withCredentials(s3Configutation.getCredentials())
				.withRegion(s3Configutation.getRegion())
				.build();

		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentType("application/json");
		PutObjectRequest request = new PutObjectRequest(
				s3Configutation.getBucketName(),
				tennatId + "." + s3Configutation.getPointsFileName(),
				new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)),
				metadata);
		request.setCannedAcl(CannedAccessControlList.PublicRead);
		s3client.putObject(request);
	}
}
