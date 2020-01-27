package org.jfl110.mylocation;

import static org.jfl110.aws.BadInputGatewayResponseException.NO_INPUT_MESSAGE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.jfl110.app.ZonedNowSupplier;
import org.jfl110.aws.GatewayEventInformation;
import org.jfl110.aws.GatewayRequestHandler;
import org.jfl110.aws.GatewayResponse;
import org.jfl110.aws.GatewayResponseException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.google.common.collect.ImmutableList;

/**
 * Tests {@link LogLocationsHandler}
 * 
 * @author jim
 *
 */
public class TestLogLocationsHandler {

	private final SecurityKeyProvider securityKeyProvider = mock(SecurityKeyProvider.class);
	private final LogLocationDAO logLocationDAO = mock(LogLocationDAO.class);
	private final ZonedNowSupplier nowSupplier = () -> ZonedDateTime.of(2012, 2, 1, 3, 4, 5, 56, ZoneId.of("UTC"));
	private final LogLocationsHandler handler = new LogLocationsHandler(logLocationDAO, securityKeyProvider, nowSupplier);
	private final GatewayEventInformation eventInfo = mock(GatewayEventInformation.class);
	private final Context context = mock(Context.class);
	private final LambdaLogger logger = mock(LambdaLogger.class);
	
	private final String someTime = "2020-01-24T20:48:43.7+01:00[Europe/Madrid]";
	private final String someTimeLater = "2020-01-24T21:49:43.7+01:00[Europe/Madrid]";
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private final ArgumentCaptor<ImmutableList<LogLocationItem>> itemsCaptor = ArgumentCaptor.forClass((Class<ImmutableList<LogLocationItem>>)(Class)ImmutableList.class);
	
	@Before
	public void setUp() {
		when(context.getLogger()).thenReturn(logger);
	}


	/**
	 * Test all invalid inputs
	 */
	@Test
	public void testBadInputs() throws IOException {
		requireException(NO_INPUT_MESSAGE, null, null, handler);
		requireException("bad-security-key", null, new ExposedLogLocationsInput(ImmutableList.of(), "provider-is-null-this-is-not", "dev1"), handler);
		
		when(securityKeyProvider.getSecurityKey()).thenReturn("a-key");
		requireException("bad-security-key", null, new ExposedLogLocationsInput(ImmutableList.of(), null, "dev1"), handler);
		requireException("bad-security-key", null, new ExposedLogLocationsInput(ImmutableList.of(), "", "dev1"), handler);
		requireException("bad-security-key", null, new ExposedLogLocationsInput(ImmutableList.of(), "not-a-key", "dev1"), handler);
		
		requireException("null-in-input", "id", new ExposedLogLocationsInput(ImmutableList.of(new ExposedLogLocationsInput.Location(null, 55.123, -4.521, 1, 2, someTime)), "a-key", "dev1"), handler);
		requireException("null-in-input", "id", new ExposedLogLocationsInput(ImmutableList.of(new ExposedLogLocationsInput.Location("", 55.123, -4.521, 1, 2, someTime)), "a-key", "dev1"), handler);
	
		requireException("null-in-input", "time", new ExposedLogLocationsInput(ImmutableList.of(new ExposedLogLocationsInput.Location("a", 55.123, -4.521, 1, 2, null)), "a-key", "dev1"), handler);
		requireException("null-in-input", "time", new ExposedLogLocationsInput(ImmutableList.of(new ExposedLogLocationsInput.Location("a", 55.123, -4.521, 1, 2 , "")), "a-key", "dev1"), handler);
	
		requireException("null-in-input", "device-name", new ExposedLogLocationsInput(ImmutableList.of(), "a-key", null), handler);
		requireException("null-in-input", "device-name", new ExposedLogLocationsInput(ImmutableList.of(), "a-key", ""), handler);
	}


	/**
	 * Test an empty save
	 */
	@Test
	public void testEmptySave() throws IOException {
		
		// Given
		ExposedLogLocationsInput input = new ExposedLogLocationsInput(ImmutableList.of(), "", "dev1");
		
		// When
		GatewayResponse response = handler.handleRequest(input, eventInfo, context);
		
		// Then
		assertEquals(200, response.getStatusCode());
		assertEquals("{\"savedIds\":[]}", response.getBody());
	}
	
	
	/**
	 * Test a simple save
	 */
	@Test
	public void testSimpleSave() throws IOException {
		// Given
		when(securityKeyProvider.getSecurityKey()).thenReturn("a-key");

		ExposedLogLocationsInput input = new ExposedLogLocationsInput(ImmutableList.of(
				new ExposedLogLocationsInput.Location("a", 55.123, -4.521, 55, 11.1f, someTime),
				new ExposedLogLocationsInput.Location("b", 56.556, -4.71, 77.8, 22.4f, someTimeLater)
				), "a-key", "dev1");
		
		// When
		GatewayResponse response = handler.handleRequest(input, eventInfo, context);
		
		// Then
		assertEquals(200, response.getStatusCode());
		assertEquals("{\"savedIds\":[\"a\",\"b\"]}", response.getBody());
		
		verify(logLocationDAO).save(itemsCaptor.capture());
		assertEquals(1, itemsCaptor.getAllValues().size());
		assertEquals(2, itemsCaptor.getValue().size());
		
		assertEquals("a", itemsCaptor.getValue().get(0).getId());
		assertEquals(55.123, itemsCaptor.getValue().get(0).getLatitude(), 0d);
		assertEquals(-4.521, itemsCaptor.getValue().get(0).getLongitude(), 0d);
		assertEquals(55, itemsCaptor.getValue().get(0).getAltitude(), 0d);
		assertEquals(11.1f, itemsCaptor.getValue().get(0).getAccuracy(), 0d);
		assertEquals("dev1", itemsCaptor.getValue().get(0).getDeviceName());
		assertEquals(someTime, itemsCaptor.getValue().get(0).getTime());
		assertEquals("2012-02-01T03:04:05.000000056Z[UTC]", itemsCaptor.getValue().get(0).getSavedTime());
		
		assertNull(itemsCaptor.getValue().get(0).getEndTime());
		
		assertEquals("b", itemsCaptor.getValue().get(1).getId());
		assertEquals(56.556, itemsCaptor.getValue().get(1).getLatitude(), 0d);
		assertEquals(-4.71, itemsCaptor.getValue().get(1).getLongitude(), 0d);
		assertEquals(77.8, itemsCaptor.getValue().get(1).getAltitude(), 0d);
		assertEquals(22.4f, itemsCaptor.getValue().get(1).getAccuracy(), 0d);
		assertEquals("dev1", itemsCaptor.getValue().get(1).getDeviceName());
		assertEquals(someTimeLater, itemsCaptor.getValue().get(1).getTime());
		assertEquals("2012-02-01T03:04:05.000000056Z[UTC]", itemsCaptor.getValue().get(1).getSavedTime());
		assertNull(itemsCaptor.getValue().get(1).getEndTime());
	}
	
	/**
	 * Test time zone difference between device and server
	 */
	@Test
	public void testTimeZones() throws IOException {
		System.out.println(ZonedDateTime.now().format(DateTimeFormatter.ISO_ZONED_DATE_TIME));
	}


	private <T> void requireException(String code, String message, T input, GatewayRequestHandler<T> handler) throws IOException {
		try {
			handler.handleRequest(input, eventInfo, context);
		} catch (GatewayResponseException e) {
			assertEquals(code, e.getCode());
			assertEquals(message, e.getDetailedMessage());
			return;
		}
		fail("No exception thrown");
	}
}