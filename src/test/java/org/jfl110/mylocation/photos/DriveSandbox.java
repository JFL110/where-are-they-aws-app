package org.jfl110.mylocation.photos;

import static org.junit.Assume.assumeTrue;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.jfl110.dynamodb.AmazonDynamoDBSupplier;
import org.jfl110.dynamodb.DynamoDBTablePrefixModule;
import org.jfl110.genericitem.DynamoGenericItemModule;
import org.jfl110.mylocation.MyLocationAppConfig;
import org.junit.Test;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.File.ImageMediaMetadata.Location;
import com.google.api.services.drive.model.FileList;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Scrape Google Drive, extract all photo location and time and insert into
 * DynamoDB
 * 
 * @author jim
 *
 */
public class DriveSandbox {

	private static final Path CREDENTIALS_PATH = Paths.get("/home/jim/source/where-are-they-aws-app/credentials-DO-NOT-COMMIT.properties");

	private static final String APPLICATION_NAME = "My Location Scraper";
	private static final String TOKENS_DIRECTORY_PATH = "tokens";
	private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE_METADATA_READONLY);
	private static final JacksonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

	private final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss");

	private static final int MAX_PAGES = 100000;

	@Test
	public void test() throws Exception {

		// Only run where credentials file is present
		assumeTrue(Files.exists(CREDENTIALS_PATH));

		// Read credentials
		Properties props = new Properties();
		try (InputStream in = Files.newInputStream(CREDENTIALS_PATH)) {
			props.load(in);
		}

		String driveCredentialsJson = props.getProperty("driveCredentials");
		String dynamoAccess = props.getProperty("driveDynamoDBAccessKey");
		String dynamoSecret = props.getProperty("driveDynamoDBSecretKey");

		// DynamoDB

		AmazonDynamoDB db = AmazonDynamoDBClientBuilder.standard().withRegion(Regions.EU_WEST_2)
				.withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(dynamoAccess, dynamoSecret)))
				.build();

		Injector injector = Guice.createInjector(
				new DynamoDBTablePrefixModule(MyLocationAppConfig.TABLE_NAME_PREFIX),
				b -> b.bind(AmazonDynamoDBSupplier.class).toInstance(() -> db),
				new DynamoGenericItemModule());

		GoogleDrivePhotoLocationDao dao = injector.getInstance(GoogleDrivePhotoLocationDao.class);

		// Get credentials

		NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

		GoogleClientSecrets clientSecrets = JSON_FACTORY.fromString(driveCredentialsJson, GoogleClientSecrets.class);

		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
				httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
						.setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
						.setAccessType("offline")
						.build();

		LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
		Credential creds = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");

		// Drive

		Drive service = new Drive.Builder(httpTransport, JSON_FACTORY, creds)
				.setApplicationName(APPLICATION_NAME)
				.build();

		String lastToken = null;
		for (int i = 0; i < MAX_PAGES; i++) {
			System.out.println("Reading page..");

			FileList filesResult = getPage(service, lastToken);

			List<File> files = filesResult.getFiles();
			files.forEach(f -> {
				String id = f.getId();
				Object metaData = f.get("imageMediaMetadata");
				if (!(metaData instanceof File.ImageMediaMetadata)) {
					return;
				}

				File.ImageMediaMetadata fileMeta = (File.ImageMediaMetadata) metaData;

				String time = fileMeta.getTime();
				Location location = fileMeta.getLocation();

				if (time == null || location == null || location.getLatitude() == null || location.getLongitude() == null) {
					// System.out.println("Image with no time / location");
					return;
				}

				LocalDateTime parsedTime = parseTime(time);

				if (parsedTime == null) {
					System.out.println("Could not parse [" + time + "]");
					return;
				}

				System.out.println("saving : " + id + " " + parsedTime + " " + location);

				dao.save("test",
						new GoogleDrivePhotoLocation(id,
								location.getLatitude(),
								location.getLongitude(),
								ZonedDateTime.of(parsedTime, ZoneId.of("UTC"))));
			});

			lastToken = filesResult.getNextPageToken();
			if (lastToken == null) {
				break;
			}
		}
	}


	private LocalDateTime parseTime(String time) {
		try {
			return LocalDateTime.parse(time, DATE_TIME_FORMAT);
		} catch (Exception e) {
			return null;
		}
	}


	private FileList getPage(Drive service, String nextPageToken) throws IOException {

		Drive.Files.List request = service.files().list()
				.setPageSize(100)
				.setQ("mimeType='image/jpeg'")
				.setFields("nextPageToken, files(id, imageMediaMetadata)");

		if (nextPageToken != null) {
			request.setPageToken(nextPageToken);
		}

		return request.execute();
	}
}
