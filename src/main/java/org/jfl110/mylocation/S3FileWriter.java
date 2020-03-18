package org.jfl110.mylocation;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import javax.inject.Inject;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

public class S3FileWriter {

	private final S3Configutation sConfigutation;

	@Inject
	S3FileWriter( S3Configutation sConfigutation) {
		this.sConfigutation = sConfigutation;
	}


	void writeJsonToPointsFile(String json) {

		AmazonS3Client s3client = new AmazonS3Client(sConfigutation.getCredentials());

		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentType("application/json");
		PutObjectRequest request = new PutObjectRequest(sConfigutation.getBucketName(), sConfigutation.getPointsFileName(), new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)),
				metadata);
		request.setCannedAcl(CannedAccessControlList.PublicRead);
		s3client.putObject(request);
	}
}
