package org.jfl110.mylocation.photos;

import java.io.IOException;

import javax.inject.Inject;

import org.jfl110.aws.GatewayEventInformation;
import org.jfl110.aws.GatewayRequestHandler;
import org.jfl110.aws.GatewayResponse;
import org.jfl110.aws.GatewayResponseBuilder;

import com.amazonaws.services.lambda.runtime.Context;

public class SyncS3PhotosHandler implements GatewayRequestHandler<String> {

	private final ProcessS3PhotoBucket processS3PhotoBucket;

	@Inject
	SyncS3PhotosHandler(ProcessS3PhotoBucket processS3PhotoBucket) {
		this.processS3PhotoBucket = processS3PhotoBucket;
	}


	@Override
	public GatewayResponse handleRequest(String securityKey, GatewayEventInformation eventInfo, Context context) throws IOException {
		processS3PhotoBucket.process();
		return GatewayResponseBuilder.gatewayResponse().ok().stringBody("ok").build();
	}


	@Override
	public Class<String> inputClazz() {
		return String.class;
	}

}
