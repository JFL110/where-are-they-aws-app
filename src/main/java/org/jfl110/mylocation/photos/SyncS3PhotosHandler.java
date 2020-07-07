package org.jfl110.mylocation.photos;

import java.io.IOException;

import javax.inject.Inject;

import org.jfl110.aws.BadInputGatewayResponseException;
import org.jfl110.aws.GatewayEventInformation;
import org.jfl110.aws.GatewayRequestHandler;
import org.jfl110.aws.GatewayResponse;
import org.jfl110.aws.GatewayResponseBuilder;
import org.jfl110.mylocation.ExposedSecurityKeyInput;
import org.jfl110.mylocation.MyLocationAppConfig;
import org.jfl110.mylocation.SecurityKeyProvider;
import org.jfl110.util.StringUtils;

import com.amazonaws.services.lambda.runtime.Context;

/**
 * Handler to process all photos stored in S3.
 * 
 * @author jim
 *
 */
public class SyncS3PhotosHandler implements GatewayRequestHandler<ExposedSecurityKeyInput> {

	private final ProcessS3PhotoBucket processS3PhotoBucket;
	private final SecurityKeyProvider securityKeyProvider;

	@Inject
	SyncS3PhotosHandler(ProcessS3PhotoBucket processS3PhotoBucket, SecurityKeyProvider securityKeyProvider) {
		this.processS3PhotoBucket = processS3PhotoBucket;
		this.securityKeyProvider = securityKeyProvider;
	}


	@Override
	public GatewayResponse handleRequest(ExposedSecurityKeyInput secretKeyInput, GatewayEventInformation eventInfo, Context context)
			throws IOException {
		if (secretKeyInput == null)
			throw new BadInputGatewayResponseException(BadInputGatewayResponseException.NO_INPUT_MESSAGE);
		if (StringUtils.isBlank(secretKeyInput.getTenantId()) ||
				StringUtils.isBlank(secretKeyInput.getSecurityKey()) ||
				!securityKeyProvider.getSecurityKey(secretKeyInput.getTenantId()).orElse("").equals(secretKeyInput.getSecurityKey())) {
			throw new BadInputGatewayResponseException(MyLocationAppConfig.BAD_SECURITY_KEY);
		}

		ExposedSyncS3PhotosOutput output = processS3PhotoBucket.process(secretKeyInput.getTenantId());
		return GatewayResponseBuilder.gatewayResponse().ok().jsonBodyFromObject(output).build();
	}
}