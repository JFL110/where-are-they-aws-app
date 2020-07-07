package org.jfl110.mylocation.status;

import java.io.IOException;

import javax.inject.Inject;

import org.jfl110.aws.GatewayEventInformation;
import org.jfl110.aws.GatewayRequestHandler;
import org.jfl110.aws.GatewayResponse;
import org.jfl110.aws.GatewayResponseBuilder;
import org.jfl110.version.VersionExtractor;

import com.amazonaws.services.lambda.runtime.Context;

/**
 * Handler that verifies the Lambda above this handler is accessible.
 * 
 * @author jim
 */
public class StatusHandler implements GatewayRequestHandler<Void> {

	private final VersionExtractor versionExtractor;

	@Inject
	StatusHandler(VersionExtractor versionExtractor) {
		this.versionExtractor = versionExtractor;
	}


	@Override
	public GatewayResponse handleRequest(Void input, GatewayEventInformation eventInfo, Context context) throws IOException {
		ExposedStatusOutput status = new ExposedStatusOutput(ExposedStatusOutput.OK, versionExtractor.getVersion().orElse(null));
		return GatewayResponseBuilder.gatewayResponse().ok().jsonBodyFromObject(status).build();
	}
}