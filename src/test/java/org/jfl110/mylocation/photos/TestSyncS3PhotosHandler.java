package org.jfl110.mylocation.photos;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.Optional;

import org.jfl110.aws.BadInputGatewayResponseException;
import org.jfl110.aws.GatewayEventInformation;
import org.jfl110.aws.GatewayResponse;
import org.jfl110.mylocation.ExposedSecurityKeyInput;
import org.jfl110.mylocation.SecurityKeyProvider;
import org.junit.Before;
import org.junit.Test;

import com.amazonaws.services.lambda.runtime.Context;

/**
 * Tests {@link SyncS3PhotosHandler}
 * 
 * @author jim
 *
 */
public class TestSyncS3PhotosHandler {

	private static final String GOOD_TENANT_ID = "good-tenant";
	private static final String SECURITY_KEY = "the-key";

	private final ProcessS3PhotoBucket processS3PhotoBucket = mock(ProcessS3PhotoBucket.class);
	private final SecurityKeyProvider securityKeyProvider = mock(SecurityKeyProvider.class);
	private final SyncS3PhotosHandler handler = new SyncS3PhotosHandler(processS3PhotoBucket, securityKeyProvider);

	private final GatewayEventInformation eventInfo = mock(GatewayEventInformation.class);
	private final Context context = mock(Context.class);

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
		GatewayResponse response = handler.handleRequest(new ExposedSecurityKeyInput(GOOD_TENANT_ID, SECURITY_KEY), eventInfo, context);

		verify(processS3PhotoBucket, times(1)).process(GOOD_TENANT_ID);
		assertEquals(200, response.getStatusCode());
		assertNotNull(response.getBody());
	}
}
