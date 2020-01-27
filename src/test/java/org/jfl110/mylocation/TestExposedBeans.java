package org.jfl110.mylocation;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.jfl110.app.DefaultResourceLoaderModule;
import org.jfl110.app.ResourceLoader;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.inject.Guice;

/**
 * Tests JSON beans parsing
 * 
 * @author jim
 *
 */
public class TestExposedBeans {

	private final ResourceLoader resLoader = Guice.createInjector(new DefaultResourceLoaderModule()).getInstance(ResourceLoader.class);
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
	
	
	@Test
	public void testExposedShowLogLocationsOutput() throws JsonParseException, JsonMappingException, IOException {
		// Given
		ExposedShowLogLocationsOutput object = new ExposedShowLogLocationsOutput(ImmutableList.of(new ExposedShowLogLocationsOutput.Location("a", 10.1, 20.2, 30.3, 40.4f, 5555l, "title", "A")));
		String asString = objectMapper.writeValueAsString(object);
		
		// When
		ExposedShowLogLocationsOutput read = objectMapper.readValue(asString, ExposedShowLogLocationsOutput.class);
		
		// Then
		assertEquals(1,  read.getLocations().size());
		assertEquals("a", read.getLocations().get(0).getId());
		assertEquals(10.1, read.getLocations().get(0).getLatitude(), 0d);
		assertEquals(20.2, read.getLocations().get(0).getLongitude(), 0d);
		assertEquals(30.3, read.getLocations().get(0).getAltitude(), 0d);
		assertEquals(40.4, read.getLocations().get(0).getAccuracy(), 0.01d);
		assertEquals(5555, read.getLocations().get(0).getRecordedTimeMillis());
		assertEquals("title", read.getLocations().get(0).getTitle());
		assertEquals("A", read.getLocations().get(0).getPointType());
	}
}