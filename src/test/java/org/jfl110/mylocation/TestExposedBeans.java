package org.jfl110.mylocation;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.jfl110.app.ResourceLoader;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Guice;

/**
 * Tests JSON beans parsing
 * 
 * @author jim
 *
 */
public class TestExposedBeans {

	private final ResourceLoader resLoader = Guice.createInjector().getInstance(ResourceLoader.class);
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Test
	public void testExposedLogLocationsInput() throws JsonParseException, JsonMappingException, IOException {
		// Given
		String inputJson = resLoader.loadAsString("TestExposedLogLocationsInput.json");
		
		// When
		ExposedLogLocationsInput input = objectMapper.readValue(inputJson, ExposedLogLocationsInput.class);
		
		// Then
		assertEquals("a-key", input.getSecurityKey());
		assertEquals("dev-1", input.getDeviceName());
		assertEquals(2, input.getLocations().size());
		
		assertEquals("a", input.getLocations().get(0).getId());
		assertEquals(5d, input.getLocations().get(0).getLatitude(), 0d);
		assertEquals(-5d, input.getLocations().get(0).getLongitude(), 0d);
		assertEquals(44.4f, input.getLocations().get(0).getAccuracy(), 0.01d);
		assertEquals(100, input.getLocations().get(0).getAltitude(), 0d);
		assertEquals("20200105T020408Z", input.getLocations().get(0).getRecordedTimeAsString());
		
		assertEquals("b", input.getLocations().get(1).getId());
		assertEquals(24.5123, input.getLocations().get(1).getLatitude(), 0d);
		assertEquals(-5.123123, input.getLocations().get(1).getLongitude(), 0d);
		assertEquals(33.3f, input.getLocations().get(1).getAccuracy(), 0.01d);
		assertEquals(0, input.getLocations().get(1).getAltitude(), 0d);
		assertEquals("20200105T020409Z", input.getLocations().get(1).getRecordedTimeAsString());
	}
}