package org.jfl110.mylocation;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.stream.Stream;

import org.jfl110.app.Logger;
import org.jfl110.aws.GatewayEventInformation;
import org.jfl110.aws.GatewayResponse;
import org.junit.Test;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;

/**
 * Tests {@link ShowAllLocationsJsonHandler}
 * 
 * @author jim
 *
 */
public class TestShowAllLocationsJsonHandler {
	
	private final LogLocationDAO logLocationDAO = mock(LogLocationDAO.class);
	private final ManualLocationsDAO manualLocationsDAO = mock(ManualLocationsDAO.class);
	private final Logger logger = mock(Logger.class);
	private final GatewayEventInformation eventInfo = mock(GatewayEventInformation.class);
	private final Context context = mock(Context.class);
	private final ShowAllLocationsJsonHandler handler = new ShowAllLocationsJsonHandler(logLocationDAO, manualLocationsDAO, logger);
	private final ObjectMapper objectMapper = new ObjectMapper();
	

	private final ZonedDateTime time = ZonedDateTime.of(2020, 1, 1, 1,1, 1, 1, ZoneId.of("UTC"));
	private final ZonedDateTime timeAfter = ZonedDateTime.of(2020, 1, 2, 1,1, 1, 1, ZoneId.of("UTC"));


	/**
	 * Tests removing duplicate entries, using most accurate one
	 */
	@Test
	public void testRemoveEntry() throws IOException {
		// Given - b has lower accuracy than a so should replace it. C has different time.
		when(logLocationDAO.listAll()).thenReturn(asStream(logItem("a", 20, 1, 2, time),  logItem("b", 5, 1, 2, time), logItem("c", 20, 1, 2, timeAfter), logItem("d", 1000, 1, 2, time)));
		
		// When
		GatewayResponse response = handler.handleRequest("", eventInfo, context);
		ExposedShowLogLocationsOutput out = objectMapper.readValue(response.getBody(), ExposedShowLogLocationsOutput.class);
		
		// Then
		assertEquals(200, response.getStatusCode());
		assertEquals(2,  out.getLocations().size());
		assertEquals("c",  out.getLocations().get(0).getId());
		assertEquals("b",  out.getLocations().get(1).getId());
	}
	
	
	/**
	 * Tests mapping values
	 */
	@Test
	public void testMapping() throws IOException {
		when(logLocationDAO.listAll()).thenReturn(asStream(logItem("a", 20.1f, 10.24, 244.4, time)));
		when(manualLocationsDAO.listAll()).thenReturn(asStream(manualItem("b", 30.1f, 40.24, 544.4, timeAfter, "my-title")));
		
		// When
		GatewayResponse response = handler.handleRequest("", eventInfo, context);
		ExposedShowLogLocationsOutput out = objectMapper.readValue(response.getBody(), ExposedShowLogLocationsOutput.class);
		
		// Then
		assertEquals(200, response.getStatusCode());
		assertEquals(2,  out.getLocations().size());
		assertEquals("a",  out.getLocations().get(0).getId());
		assertEquals(20.1,  out.getLocations().get(0).getAccuracy(), 0.001d);
		assertEquals(10.24,  out.getLocations().get(0).getLatitude(), 0d);
		assertEquals(244.4,  out.getLocations().get(0).getLongitude(), 0d);
		assertEquals(1577840461000l,  out.getLocations().get(0).getRecordedTimeMillis());

		assertEquals("b",  out.getLocations().get(1).getId());
		assertEquals(30.1,  out.getLocations().get(1).getAccuracy(), 0.001d);
		assertEquals(40.24,  out.getLocations().get(1).getLatitude(), 0d);
		assertEquals(544.4,  out.getLocations().get(1).getLongitude(), 0d);
		assertEquals(1577926861000l,  out.getLocations().get(1).getRecordedTimeMillis());
	}
	
	
	private <T> Stream<T> asStream(@SuppressWarnings("unchecked") T... items){
		return ImmutableList.copyOf(items).stream();
	}
	
	private LogLocationItem logItem(String id, float accuracy, double lat, double longitude, ZonedDateTime time) {
		LogLocationItem item = new LogLocationItem();
		item.setId(id);
		item.setDeviceName("dev-1");
		item.setAccuracy(accuracy);
		item.setLatitude(lat);
		item.setLongitude(longitude);
		item.setTime(LogLocationItem.DATE_FORMAT.format(time));
		// TODO OTHER PROPS
		return item;
	}
	
	
	private ManualLocationItem manualItem(String id, float accuracy, double lat, double longitude, ZonedDateTime time, String title) {
		ManualLocationItem item = new ManualLocationItem();
		item.setId(id);
		item.setAccuracy(accuracy);
		item.setLatitude(lat);
		item.setLongitude(longitude);
		item.setTime(LogLocationItem.DATE_FORMAT.format(time));
		item.setTitle(title);
		return item;
	}
}