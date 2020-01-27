package org.jfl110.mylocation;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import java.io.IOException;

import org.jfl110.aws.GatewayEventInformation;
import org.jfl110.aws.GatewayResponse;
import org.junit.Test;

import com.amazonaws.services.lambda.runtime.Context;


/**
 * Simple test of PokeHandler
 * 
 * @author jim
 *
 */
public class TestPokeHandler {

	/**
	 * Verify the handler outputs a 200 with 'Poke accepted'
	 */
	@Test
	public void testPoke() throws IOException {
		GatewayResponse response = new PokeHandler().handleRequest("", mock(GatewayEventInformation.class), mock(Context.class));
		assertEquals("Poke accepted", response.getBody());
		assertEquals(200, response.getStatusCode());
	}
}