//package org.jfl110.mylocation.photos;
//
//import java.io.ByteArrayInputStream;
//import java.nio.file.Files;
//
//import org.jfl110.mylocation.S3Configutation;
//import org.jfl110.util.ExceptionUtils;
//
//import com.amazonaws.services.s3.AmazonS3Client;
//import com.amazonaws.services.s3.model.CannedAccessControlList;
//import com.amazonaws.services.s3.model.ObjectMetadata;
//import com.amazonaws.services.s3.model.PutObjectRequest;
//
//public class PhotoUploader {
//
//	private final String photoDir = "photos/";
//	private final ExtractPhotosFromDirectory extractPhotosFromDirectory;
//	private final S3Configutation s3Config;
//	
//	public PhotoUploader(ExtractPhotosFromDirectory extractPhotosFromDirectory, S3Configutation s3Config) {
//		this.extractPhotosFromDirectory = extractPhotosFromDirectory;
//		this.s3Config = s3Config;
//	}
//
//	public void upload(String dir) {
//		AmazonS3Client s3client = new AmazonS3Client(s3Config.getCredentials());
//
//		extractPhotosFromDirectory.extract(dir).forEach(p -> {
//			
//			// Put photo
//			ObjectMetadata metadata = new ObjectMetadata();
//			metadata.setContentType("application/json");
//			PutObjectRequest request = new PutObjectRequest(s3Config.getBucketName(), photoDir + p.getPath().getFileName().toString(),
//					new ByteArrayInputStream(ExceptionUtils.doRethrowing(() -> Files.readAllBytes(p.getPath()))), metadata);
//			request.setCannedAcl(CannedAccessControlList.PublicRead);
//			s3client.putObject(request);
//			
//			String path ="https://" +s3Config.getBucketName() + ".eu-west-2.amazonaws.com/" + photoDir + p.getPath().getFileName();
//			System.out.println(path);
//		});
//	}
//}