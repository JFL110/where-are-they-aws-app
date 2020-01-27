package org.jfl110.mylocation;

import java.io.IOException;

import org.jfl110.aws.GatewayEventInformation;
import org.jfl110.aws.GatewayRequestHandler;
import org.jfl110.aws.GatewayResponse;
import org.jfl110.aws.GatewayResponseBuilder;

import com.amazonaws.services.lambda.runtime.Context;

/**
 * Handler that verifies the Lambda above this handler is accessible.
 * 
 * @author jim
 */
class PokeHandler implements GatewayRequestHandler<String> {

	@Override
	public GatewayResponse handleRequest(String input, GatewayEventInformation eventInfo, Context context) throws IOException {
		return GatewayResponseBuilder.gatewayResponse().ok().stringBody("Poke accepted").build();
	}


	@Override
	public Class<String> inputClazz() {
		return String.class;
	}
}