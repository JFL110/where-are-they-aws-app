package org.jfl110.mylocation;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

import org.jfl110.aws.BadInputGatewayResponseException;
import org.jfl110.aws.GatewayEventInformation;
import org.jfl110.aws.GatewayResponse;
import org.jfl110.mylocation.photos.Photo;
import org.jfl110.mylocation.photos.PhotoDao;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.amazonaws.services.lambda.runtime.Context;
import com.google.common.collect.ImmutableList;

/**
 * Tests {@link WriteAllPointsSummaryToS3Handler}
 * 
 * @author jim
 *
 */
public class TestWriteAllPointsSummaryToS3Handler {

	private static final String GOOD_TENANT_ID = "good-tenant";
	private static final String SECURITY_KEY = "the-key";

	private final SecurityKeyProvider securityKeyProvider = mock(SecurityKeyProvider.class);
	private final S3FileWriter s3FileWriter = mock(S3FileWriter.class);
	private final LocationsDao logLocationDAO = mock(LocationsDao.class);
	private final PhotoDao photoDao = mock(PhotoDao.class);
	private final WriteAllPointsSummaryToS3Handler handler = new WriteAllPointsSummaryToS3Handler(
			s3FileWriter,
			logLocationDAO,
			securityKeyProvider,
			photoDao);

	private final GatewayEventInformation eventInfo = mock(GatewayEventInformation.class);
	private final Context context = mock(Context.class);
	private final ZonedDateTime testTime = ZonedDateTime.of(2019, 1, 1, 6, 12, 12, 0, ZoneId.of("UTC"));

	@Before
	public void setUp() {
		when(securityKeyProvider.getSecurityKey(GOOD_TENANT_ID)).thenReturn(Optional.of(SECURITY_KEY));
	}


	@Test(expected = BadInputGatewayResponseException.class)
	public void testEmptyInput() throws IOException {
		handler.handleRequest(null, eventInfo, context);
	}


	@Test(expected = BadInputGatewayResponseException.class)
	public void testEmptyTenantId() throws IOException {
		handler.handleRequest(new ExposedSecurityKeyInput("", "a-key"), eventInfo, context);
	}


	@Test(expected = BadInputGatewayResponseException.class)
	public void testEmptySecurityKey() throws IOException {
		handler.handleRequest(new ExposedSecurityKeyInput("an-id", ""), eventInfo, context);
	}


	@Test(expected = BadInputGatewayResponseException.class)
	public void testBadTenantId() throws IOException {
		handler.handleRequest(new ExposedSecurityKeyInput("bad-id", "the-key"), eventInfo, context);
	}


	@Test(expected = BadInputGatewayResponseException.class)
	public void testBadSecurityKey() throws IOException {
		handler.handleRequest(new ExposedSecurityKeyInput(GOOD_TENANT_ID, "bad-key"), eventInfo, context);
	}


	@Test
	public void testGoodInput() throws IOException {
		// Given
		when(logLocationDAO.listAllAuto(GOOD_TENANT_ID)).thenReturn(ImmutableList.of(
				new AutoLogLocation("id-1", 55.5, 22.2, 45.3, 21.2f, "dev-1", testTime, testTime.plusMinutes(1), Optional.empty())).stream());

		when(logLocationDAO.listAllManual(GOOD_TENANT_ID)).thenReturn(ImmutableList.of(
				new ManualLogLocation("id-2", Optional.empty(), Optional.empty(), 123.3, 99.9, Optional.of(55f), testTime.plusDays(1), true,
						Optional.empty()))
				.stream());

		when(photoDao.listAll(GOOD_TENANT_ID)).thenReturn(ImmutableList.of(
				new Photo("id-3", 88.8, 55.2, "http://photo", Optional.empty(), testTime.minusHours(1), Optional.empty(), Optional.empty()))
				.stream());

		GatewayResponse response = handler.handleRequest(new ExposedSecurityKeyInput(GOOD_TENANT_ID, SECURITY_KEY), eventInfo, context);

		ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
		verify(s3FileWriter, times(1)).writeJsonToPointsFile(stringCaptor.capture());

		assertEquals(
				"{\"points\":[{\"l\":55.5,\"g\":22.2},{\"l\":123.3,\"g\":99.9}],\"photos\":[{\"url\":\"http://photo\",\"point\":{\"l\":88.8,\"g\":55.2},\"time\":1546319532000}],\"mostRecentPoint\":{\"l\":123.3,\"g\":99.9},\"mostRecentPointTime\":1546409532000}",
				stringCaptor.getValue());

		assertEquals(200, response.getStatusCode());
		assertEquals("2 points written", response.getBody());
	}
}
