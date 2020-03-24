package org.jfl110.mylocation.photos;

import java.io.IOException;

import javax.inject.Inject;

import org.jfl110.aws.BadInputGatewayResponseException;
import org.jfl110.aws.GatewayEventInformation;
import org.jfl110.aws.GatewayRequestHandler;
import org.jfl110.aws.GatewayResponse;
import org.jfl110.aws.GatewayResponseBuilder;
import org.jfl110.mylocation.SecurityKeyProvider;

import com.amazonaws.services.lambda.runtime.Context;
import com.google.common.base.Strings;

public class SyncS3PhotosHandler implements GatewayRequestHandler<String> {

	private final ProcessS3PhotoBucket processS3PhotoBucket;
	private final SecurityKeyProvider securityKeyProvider;

	@Inject
	SyncS3PhotosHandler(ProcessS3PhotoBucket processS3PhotoBucket, SecurityKeyProvider securityKeyProvider) {
		this.processS3PhotoBucket = processS3PhotoBucket;
		this.securityKeyProvider = securityKeyProvider;
	}


	@Override
	public GatewayResponse handleRequest(String securityKey, GatewayEventInformation eventInfo, Context context) throws IOException {
		if (!Strings.nullToEmpty(securityKeyProvider.getSecurityKey()).equals(Strings.nullToEmpty(securityKey))) {
			throw new BadInputGatewayResponseException("bad-security-key");
		}
		ExposedSyncS3PhotosOutput output = processS3PhotoBucket.process();
		return GatewayResponseBuilder.gatewayResponse().ok().jsonBodyFromObject(output).build();
	}


	@Override
	public Class<String> inputClazz() {
		return String.class;
	}

}
