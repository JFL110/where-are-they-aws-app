package org.jfl110.mylocation.status;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Optional;

import org.jfl110.aws.GatewayEventInformation;
import org.jfl110.aws.GatewayResponse;
import org.jfl110.version.VersionExtractor;
import org.junit.Test;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Tests {@link StatusHandler}
 * 
 * @author jim
 *
 */
public class TestStatusHandler {

	private final VersionExtractor versionExtractor = mock(VersionExtractor.class);
	private final StatusHandler handler = new StatusHandler(versionExtractor);
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Test
	public void testWithVersion() throws IOException {
		when(versionExtractor.getVersion()).thenReturn(Optional.of("1.2.3"));
		GatewayResponse response = handler.handleRequest(null, mock(GatewayEventInformation.class), mock(Context.class));
		ExposedStatusOutput status = objectMapper.readValue(response.getBody(), ExposedStatusOutput.class);

		assertEquals(200, response.getStatusCode());
		assertEquals("ok", status.getStatus());
		assertEquals("1.2.3", status.getVersion());
	}


	@Test
	public void testNoVersion() throws IOException {
		when(versionExtractor.getVersion()).thenReturn(Optional.empty());
		GatewayResponse response = handler.handleRequest(null, mock(GatewayEventInformation.class), mock(Context.class));
		ExposedStatusOutput status = objectMapper.readValue(response.getBody(), ExposedStatusOutput.class);

		assertEquals(200, response.getStatusCode());
		assertEquals("ok", status.getStatus());
		assertEquals(null, status.getVersion());
	}
}